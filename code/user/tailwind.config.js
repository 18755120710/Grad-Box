/** @type {import('tailwindcss').Config} */
module.exports = {
    content: [
        "./components/**/*.{js,vue,ts}",
        "./layouts/**/*.vue",
        "./pages/**/*.vue",
        "./plugins/**/*.{js,ts}",
        "./app.vue",
        "./error.vue",
    ],
    theme: {
        extend: {
            colors: {
                primary: {
                    DEFAULT: '#2563EB',
                    foreground: '#FFFFFF',
                },
                secondary: {
                    DEFAULT: '#64748B',
                    foreground: '#FFFFFF',
                },
                success: '#10B981',
                warning: '#F59E0B',
            },
        },
    },
    plugins: [],
}
