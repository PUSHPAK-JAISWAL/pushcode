/** @type {import('tailwindcss').Config} */
export default {
  content: [
    "./index.html",
    "./src/**/*.{js,ts,jsx,tsx}", // Add this if your JS is in a src folder
    "./main.js"
  ],
  theme: {
    extend: {},
  },
  plugins: [],
}