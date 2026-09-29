<template>
  <div class="panel w-full p-6 sm:p-7">
    <div class="mb-6 text-center lg:hidden">
      <span class="mx-auto mb-3 grid h-12 w-12 place-items-center rounded-md bg-workshop-blue text-white">
        <Wrench class="h-6 w-6" />
      </span>
      <h1 class="text-2xl font-black">Portal del taller</h1>
      <p class="text-sm text-slate-500">Acceso seguro del personal</p>
    </div>

    <div class="mb-6 grid grid-cols-2 gap-2 rounded-lg bg-slate-100 p-1">
      <button type="button" :class="tabClass('login')" @click="selectMode('login')">Login</button>
      <button type="button" :class="tabClass('recover')" @click="selectMode('recover')">Recuperar</button>
    </div>

    <div v-if="sessionUser" class="space-y-4">
      <div class="rounded-lg border border-emerald-200 bg-emerald-50 p-4">
        <p class="text-sm font-bold text-emerald-800">Sesion iniciada correctamente</p>
        <p class="mt-1 text-sm text-emerald-700">{{ sessionUser.name }} · {{ roleLabel(sessionUser.role) }}</p>
      </div>
      <button class="btn-secondary w-full" type="button" @click="$emit('logout')">
        <LogOut class="h-4 w-4" />
        Cerrar sesion
      </button>
    </div>

    <form v-else-if="mode === 'login'" class="space-y-4" @submit.prevent="login">
      <div>
        <h2 class="text-2xl font-black">Iniciar sesion</h2>
        <p class="mt-1 text-sm text-slate-500">Usa una cuenta activa del taller.</p>
      </div>
      <div>
        <label class="label">Correo</label>
        <input v-model.trim="loginForm.email" class="field mt-1" autocomplete="email" type="email" placeholder="dueno@taller.com" required />
      </div>
      <div class="relative">
        <label class="label">Contrasena</label>
        <input v-model="loginForm.password" class="field mt-1 pr-11" autocomplete="current-password" :type="showPassword ? 'text' : 'password'" placeholder="••••••••" required />
        <button class="absolute bottom-0 right-0 grid h-10 w-10 place-items-center text-slate-500 hover:text-workshop-blue" type="button" :aria-label="showPassword ? 'Ocultar contrasena' : 'Mostrar contrasena'" :title="showPassword ? 'Ocultar contrasena' : 'Mostrar contrasena'" @click="showPassword = !showPassword">
          <EyeOff v-if="showPassword" class="h-4 w-4" />
          <Eye v-else class="h-4 w-4" />
        </button>
      </div>
      <button class="btn-primary w-full" type="submit" :disabled="loading">
        <LoaderCircle v-if="loading" class="h-4 w-4 animate-spin" />
        <ShieldCheck v-else class="h-4 w-4" />
        {{ loading ? 'Validando...' : 'Iniciar sesion segura' }}
      </button>
    </form>

    <form v-else class="space-y-4" @submit.prevent="recover">
      <div>
        <h2 class="text-2xl font-black">Recuperar contrasena</h2>
        <p class="mt-1 text-sm text-slate-500">Genera una solicitud auditada de restablecimiento.</p>
      </div>
      <div>
        <label class="label">Correo registrado</label>
        <input v-model.trim="recoverEmail" class="field mt-1" autocomplete="email" type="email" placeholder="usuario@taller.com" required />
      </div>
      <button class="btn-primary w-full" type="submit" :disabled="loading">
        <LoaderCircle v-if="loading" class="h-4 w-4 animate-spin" />
        <KeyRound v-else class="h-4 w-4" />
        {{ loading ? 'Enviando...' : 'Enviar recuperacion' }}
      </button>
    </form>

    <p v-if="message" class="mt-4 rounded-md bg-emerald-50 p-3 text-sm text-emerald-700">{{ message }}</p>
    <p v-if="error" class="mt-4 rounded-md bg-red-50 p-3 text-sm text-red-700">{{ error }}</p>
  </div>
</template>

<script setup>
import { reactive, ref } from 'vue'
import { Eye, EyeOff, KeyRound, LoaderCircle, LogOut, ShieldCheck, Wrench } from 'lucide-vue-next'
import { apiRequest } from '../services/api'

const props = defineProps({
  mode: { type: String, required: true },
  sessionUser: { type: Object, default: null }
})

const emit = defineEmits(['mode', 'authenticated', 'logout'])
const message = ref('')
const error = ref('')
const loading = ref(false)
const recoverEmail = ref('')
const showPassword = ref(false)

const loginForm = reactive({ email: '', password: '' })

function tabClass(tab) {
  return [
    'rounded-md px-2 py-2 text-sm font-semibold transition',
    props.mode === tab ? 'bg-white text-workshop-blue shadow-sm' : 'text-slate-600 hover:text-workshop-blue'
  ]
}

function selectMode(mode) {
  message.value = ''
  error.value = ''
  emit('mode', mode)
}

function roleLabel(role) {
  return {
    OWNER: 'Dueno',
    MANAGER: 'Gerente',
    SECRETARY: 'Secretaria',
    MECHANIC: 'Mecanico',
    ACCOUNTANT: 'Contador',
    ADMINISTRATOR: 'Administrador del sistema',
    RECEPTIONIST: 'Recepcionista'
  }[role] || role
}

async function login() {
  error.value = ''
  message.value = ''
  loading.value = true
  try {
    const session = await apiRequest('/auth/login', {
      method: 'POST',
      body: JSON.stringify(loginForm)
    })
    emit('authenticated', session)
  } catch (err) {
    error.value = err.message || 'No se pudo iniciar sesion.'
  } finally {
    loading.value = false
  }
}

async function recover() {
  error.value = ''
  message.value = ''
  loading.value = true
  try {
    await apiRequest('/auth/password/forgot', {
      method: 'POST',
      body: JSON.stringify({ email: recoverEmail.value })
    })
    message.value = 'Solicitud registrada. Revisa el flujo de recuperacion configurado.'
  } catch (err) {
    error.value = err.message || 'No se pudo solicitar recuperacion.'
  } finally {
    loading.value = false
  }
}
</script>
