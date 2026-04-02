<template>
  <div class="min-h-screen bg-[#fafaf9] selection:bg-indigo-500/10 flex flex-col font-body text-[#0c0a09]">
    <!-- Sophisticated Liquid Glass Background (Pro Max V2) -->
    <div class="fixed inset-0 pointer-events-none overflow-hidden z-0">
      <!-- Primary Liquid Orb -->
      <div class="absolute -top-[15%] -right-[10%] w-[60%] h-[60%] bg-indigo-500/5 rounded-full blur-[120px] animate-liquid-slow"></div>
      <!-- Secondary Liquid Orb -->
      <div class="absolute -bottom-[20%] -left-[15%] w-[70%] h-[70%] bg-emerald-500/5 rounded-full blur-[140px] animate-liquid-fast"></div>
      <!-- Accent Hint -->
      <div class="absolute top-[20%] left-[10%] w-[30%] h-[30%] bg-indigo-400/3 rounded-full blur-[100px] animate-pulse-slow"></div>
    </div>
    
    <!-- Main Content Area -->
    <main class="flex-grow flex items-center justify-center p-6 relative z-10">
      <div class="w-full max-w-[1200px] mx-auto flex items-center justify-center">
        <div class="w-full max-w-md">
          <slot />
        </div>
      </div>
    </main>

    <!-- Refined Minimal Footer -->
    <footer class="py-12 text-center relative z-10 px-6">
      <div class="max-w-md mx-auto space-y-4">
        <div class="h-px bg-gradient-to-r from-transparent via-slate-200 to-transparent opacity-50"></div>
        <p class="text-[10px] font-bold text-slate-400 uppercase tracking-[0.3em] font-body opacity-60 italic">
          © 2024 <span class="text-slate-600 font-black">GradBox Intelligence</span>. Elevating Academic Excellence.
        </p>
      </div>
    </footer>
  </div>
</template>

<script setup>
const authStore = useAuthStore()

onMounted(() => {
  // Visit Tracking for DAU
  const api = useApi()
  api('/api/admin/stats/visit', {
    method: 'POST',
    body: { userId: authStore.userInfo?.id }
  }).catch(() => {})
})

// Using Crimson Pro for headings and Atkinson Hyperlegible for body
useHead({
  link: [
    {
      rel: 'stylesheet',
      href: 'https://fonts.googleapis.com/css2?family=Atkinson+Hyperlegible:wght@400;700&family=Crimson+Pro:wght@400;500;600;700&display=swap'
    }
  ]
})
</script>

<style>
:root {
  --font-heading: 'Crimson Pro', serif;
  --font-body: 'Atkinson Hyperlegible', sans-serif;
}

.font-heading { font-family: var(--font-heading); }
.font-body { font-family: var(--font-body); }

body {
  font-family: var(--font-body);
}

h1, h2, h3, h4, .font-display {
  font-family: var(--font-heading);
}
</style>

<style scoped>
@keyframes liquid-slow {
  0%, 100% { transform: translate(0, 0) scale(1); }
  33% { transform: translate(30px, -50px) scale(1.1); }
  66% { transform: translate(-20px, 20px) scale(0.9); }
}

@keyframes liquid-fast {
  0%, 100% { transform: translate(0, 0) scale(1); }
  50% { transform: translate(-40px, 30px) scale(1.15); }
}

@keyframes pulse-slow {
  0%, 100% { opacity: 0.2; }
  50% { opacity: 0.5; }
}

.animate-liquid-slow { animation: liquid-slow 20s ease-in-out infinite; }
.animate-liquid-fast { animation: liquid-fast 15s ease-in-out infinite; }
.animate-pulse-slow { animation: pulse-slow 10s ease-in-out infinite; }
</style>
