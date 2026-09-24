<template>
  <main class="grid min-h-screen bg-[radial-gradient(circle_at_top_left,#dff6f1_0,#f4f7fb_34%,#e9eef5_100%)] px-4 py-6 sm:px-6 lg:px-8">
    <section class="mx-auto grid w-full max-w-6xl items-center gap-8 lg:grid-cols-[1fr_430px]">
      <div class="hidden lg:block">
        <div class="max-w-xl">
          <div class="mb-8 inline-flex items-center gap-3 rounded-md border border-white/70 bg-white/80 px-4 py-3 shadow-sm backdrop-blur">
            <span class="grid h-10 w-10 place-items-center rounded-md bg-workshop-blue text-white">
              <Wrench class="h-5 w-5" />
            </span>
            <span>
              <span class="block text-sm font-black uppercase tracking-wide text-workshop-steel">Taller mecanico</span>
              <span class="text-sm text-slate-500">Gestion segura de recepcion</span>
            </span>
          </div>

          <h1 class="text-5xl font-black leading-tight text-workshop-ink">Acceso al portal del taller</h1>
          <p class="mt-5 max-w-lg text-base leading-7 text-slate-600">
            Ingreso protegido para personal autorizado. Cada acceso, registro y recuperacion queda auditado en el backend.
          </p>

          <div class="mt-8 grid max-w-lg grid-cols-3 gap-3">
            <div v-for="item in trustItems" :key="item.label" class="rounded-lg border border-white/80 bg-white/80 p-4 shadow-sm backdrop-blur">
              <component :is="item.icon" class="mb-3 h-5 w-5 text-workshop-teal" />
              <p class="text-sm font-bold text-workshop-ink">{{ item.label }}</p>
            </div>
          </div>
        </div>
      </div>

      <AuthPanel :mode="view" :session-user="user" @mode="view = $event" @authenticated="handleAuthenticated" @logout="logout" />
    </section>
  </main>
</template>

<script setup>
import { ref } from 'vue'
import { ClipboardCheck, LockKeyhole, ShieldCheck, Wrench } from 'lucide-vue-next'
import AuthPanel from './components/AuthPanel.vue'

const view = ref('login')
const user = ref(JSON.parse(localStorage.getItem('user') || 'null'))

const trustItems = [
  { label: 'JWT seguro', icon: LockKeyhole },
  { label: 'Roles activos', icon: ShieldCheck },
  { label: 'Logs auditados', icon: ClipboardCheck }
]

function handleAuthenticated(session) {
  localStorage.setItem('token', session.token)
  localStorage.setItem('user', JSON.stringify(session.user))
  user.value = session.user
  view.value = 'dashboard'
}

function logout() {
  localStorage.removeItem('token')
  localStorage.removeItem('user')
  user.value = null
  view.value = 'login'
}
</script>
