import { Terminal } from 'xterm';
import { FitAddon } from 'xterm-addon-fit';
import 'xterm/css/xterm.css';

const term = new Terminal({
    cursorBlink: true,
    theme: { 
        background: '#0b0e14', 
        foreground: '#ffffff',
        cursor: '#528bff'
    },
    fontFamily: '"Fira Code", monospace',
    fontSize: 14,
    convertEol: true 
});

const fitAddon = new FitAddon();
term.loadAddon(fitAddon);
term.open(document.getElementById('terminal-container'));
fitAddon.fit();

let socket = null;
let inputBuffer = "";

// ✅ FIX 1: Registered ONCE outside connectWebSocket to prevent
// multiple listeners stacking up on each run (caused double/triple typing)
term.onData(data => {
    if (!socket || socket.readyState !== WebSocket.OPEN) return;

    if (data === '\r') {                        // ↩️ ENTER
        socket.send(inputBuffer + "\n");
        inputBuffer = "";
        term.write('\r\n');

    } else if (data === '\u007f') {             // ⌫ BACKSPACE
        if (inputBuffer.length > 0) {
            inputBuffer = inputBuffer.slice(0, -1);
            term.write('\b \b');
        }

    } else {
        inputBuffer += data;
        term.write(data);                       // local echo
    }
});

document.getElementById('runBtn').onclick = async () => {
    const code = document.getElementById('editor').value;
    const language = document.getElementById('langSelect').value;

    term.clear();
    term.writeln('\x1b[33m[*] Sending execution request...\x1b[0m');
    inputBuffer = "";

    try {
        const res = await fetch('http://localhost:8080/api/execute', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ language, code })
        });
        const { sessionId } = await res.json();
        connectWebSocket(sessionId);
    } catch (err) {
        term.writeln('\x1b[31m[!] Error: ' + err.message + '\x1b[0m');
    }
};

function connectWebSocket(sessionId) {
    if (socket) {
        // ✅ FIX 2: Null out handlers BEFORE closing so the old onclose
        // doesn't fire after the new socket is assigned and wipe it out.
        // This was causing the "2 clicks to run" bug.
        socket.onclose = null;
        socket.onerror = null;
        socket.close();
        socket = null;
    }

    socket = new WebSocket(`ws://localhost:8080/terminal?sessionId=${sessionId}`);

    socket.onopen = () => {
        document.getElementById('status').innerText = 'CONNECTED';
        term.focus();
    };

    socket.onmessage = (e) => {
        term.write(e.data);
    };

    socket.onclose = () => {
        document.getElementById('status').innerText = 'DISCONNECTED';
        socket = null;
        inputBuffer = "";  // ✅ Clear stale buffer on disconnect
    };

    socket.onerror = (err) => {
        term.writeln('\x1b[31m[!] WebSocket error\x1b[0m');
        console.error('WebSocket error:', err);
    };
}

window.onresize = () => fitAddon.fit();