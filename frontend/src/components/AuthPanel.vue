<template>
  <div class="panel w-full p-6 sm:p-7">
    <div class="mb-6 text-center lg:hidden">
      <span class="mx-auto mb-3 grid h-12 w-12 place-items-center rounded-md bg-workshop-blue text-white">
        <Wrench class="h-6 w-6" />
      </span>
      <h1 class="text-2xl font-black">Portal del taller</h1>
      <p class="text-sm text-slate-500">Acceso seguro del personal</p>
    </div>

    <div class="mb-6 grid grid-cols-3 gap-2 rounded-lg bg-slate-100 p-1">
      <button type="button" :class="tabClass('login')" @click="selectMode('login')">Login</button>
      <button type="button" :class="tabClass('register')" @click="selectMode('register')">Registro</button>
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
      <div>
        <label class="label">Contrasena</label>
        <input v-model="loginForm.password" class="field mt-1" autocomplete="current-password" type="password" placeholder="••••••••" required />
      </div>
      <button class="btn-primary w-full" type="submit" :disabled="loading">
        <LoaderCircle v-if="loading" class="h-4 w-4 animate-spin" />
        <ShieldCheck v-else class="h-4 w-4" />
        {{ loading ? 'Validando...' : 'Iniciar sesion segura' }}
      </button>
    </form>

    <form v-else-if="mode === 'register'" class="space-y-4" @submit.prevent="register">
      <div>
        <h2 class="text-2xl font-black">Registrar usuario</h2>
        <p class="mt-1 text-sm text-slate-500">Crea usuarios internos con rol operativo.</p>
      </div>
      <div class="grid gap-4 sm:grid-cols-2">
        <div>
          <label class="label">Nombre</label>
          <input v-model.trim="registerForm.name" class="field mt-1" autocomplete="name" placeholder="Ana Lopez" required />
        </div>
        <div>
          <label class="label">Rol</label>
          <select v-model="registerForm.role" class="field mt-1">
            <option value="OWNER">Dueno</option>
            <option value="MANAGER">Gerente</option>
            <option value="SECRETARY">Secretaria</option>
            <option value="MECHANIC">Mecanico</option>
            <option value="ACCOUNTANT">Contador</option>
          </select>
        </div>
      </div>
      <div>
        <label class="label">Correo</label>
        <input v-model.trim="registerForm.email" class="field mt-1" autocomplete="email" type="email" required />
      </div>
      <div>
        <label class="label">Contrasena</label>
        <input v-model="registerForm.password" class="field mt-1" autocomplete="new-password" type="password" required minlength="8" />
      </div>
      <button class="btn-primary w-full" type="submit" :disabled="loading">
        <LoaderCircle v-if="loading" class="h-4 w-4 animate-spin" />
        <UserPlus v-else class="h-4 w-4" />
        {{ loading ? 'Creando...' : 'Crear usuario' }}
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
import { KeyRound, LoaderCircle, LogOut, ShieldCheck, UserPlus, Wrench } from 'lucide-vue-next'
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

const loginForm = reactive({ email: 'dueno@taller.com', password: 'Nerv_owner_2026!' })
const registerForm = reactive({ name: '', email: '', password: '', role: 'SECRETARY' })

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
    ACCOUNTANT: 'Contador'
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

async function register() {
  error.value = ''
  message.value = ''
  loading.value = true
  try {
    await apiRequest('/auth/register', {
      method: 'POST',
      body: JSON.stringify(registerForm)
    })
    message.value = 'Usuario registrado. Ya puede iniciar sesion.'
    emit('mode', 'login')
  } catch (err) {
    error.value = err.message || 'No se pudo registrar el usuario.'
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
