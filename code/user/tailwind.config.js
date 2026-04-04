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
                    DEFAULT: '#4f46e5', /* Indigo 600 - Unified Brand Primary */
                    foreground: '#FFFFFF',
                },
                secondary: {
                    DEFAULT: '#10b981', /* Emerald 500 - Unified Brand Secondary */
                    foreground: '#FFFFFF',
                },
                // OKLCH Semantic Palette
                success: 'oklch(65% 0.15 150)',
                error: 'oklch(60% 0.18 20)',
                warning: 'oklch(75% 0.14 80)',
                info: 'oklch(55% 0.18 260)',
                // Categorical Genre Palette
                genre: {
                    cs: 'oklch(60% 0.18 250)', // Computer Science (Blue-ish)
                    art: 'oklch(65% 0.2 330)', // Art/Design (Pink-ish)
                    mgt: 'oklch(70% 0.15 180)', // Management (Teal-ish)
                    sci: 'oklch(60% 0.15 100)', // Science (Lime-ish)
                    other: 'oklch(65% 0.1 200)' // Other (Slate-ish)
                },
                profound: {
                    black: '#020617',
                    surface: '#0a0f1e',
                    glass: 'rgba(255, 255, 255, 0.02)',
                    tint: 'oklch(98% 0.005 250)', // Very subtle cool tint for backgrounds
                }
            },
            fontFamily: {
                sans: ['Figtree', 'Inter', 'system-ui', 'sans-serif'],
                heading: ['Outfit', 'sans-serif'],
                display: ['Outfit', 'sans-serif'],
            },
            fontSize: {
                'display': ['clamp(5rem, 12vw, 15rem)', { lineHeight: '0.85', letterSpacing: '-0.04em', fontWeight: '800' }],
                'heading-xl': ['clamp(2.5rem, 6vw, 4.5rem)', { lineHeight: '1.0', letterSpacing: '-0.02em', fontWeight: '800' }],
                'heading-lg': ['clamp(1.5rem, 4vw, 2.75rem)', { lineHeight: '1.1', letterSpacing: '-0.01em', fontWeight: '700' }],
            },
            letterSpacing: {
                'profound': '0.3em',
                'tighter-profound': '-0.04em',
            },
            borderRadius: {
                '3xl': '24px',
                '4xl': '32px',
                '5xl': '48px',
            },
            boxShadow: {
                '3xl': '0 35px 60px -15px rgba(0, 0, 0, 0.08)',
            },
            keyframes: {
                float: {
                    '0%, 100%': { transform: 'translateY(0)' },
                    '50%': { transform: 'translateY(-20px)' },
                },
                'fade-in-up': {
                    '0%': { opacity: '0', transform: 'translateY(20px)' },
                    '100%': { opacity: '1', transform: 'translateY(0)' },
                },
                'fade-in': {
                    '0%': { opacity: '0' },
                    '100%': { opacity: '1' },
                },
                shake: {
                    '0%, 100%': { transform: 'translateX(0)' },
                    '25%': { transform: 'translateX(-5px)' },
                    '75%': { transform: 'translateX(5px)' },
                }
            },
            animation: {
                float: 'float 6s ease-in-out infinite',
                'fade-in-up': 'fade-in-up 0.8s ease-out forwards',
                'fade-in': 'fade-in 1s ease-out forwards',
                shake: 'shake 0.4s ease-in-out',
            }
        },
    },
    plugins: [
        require('@tailwindcss/typography'),
    ],
}
