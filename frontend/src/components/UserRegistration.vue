<template>
  <section class="mx-auto w-full max-w-3xl px-4 py-8 sm:px-6">
    <div v-if="!isAdministrator" class="panel p-6 text-sm text-red-700">
      Solo el Administrador del sistema puede crear usuarios internos.
    </div>
    <form v-else class="panel grid gap-5 p-6 sm:p-8" @submit.prevent="submit">
      <div>
        <p class="text-sm font-bold text-workshop-teal">Sucursal {{ sessionUser.branchId }}</p>
        <h1 class="text-2xl font-black text-workshop-ink">Nuevo usuario interno</h1>
        <p class="mt-1 text-sm text-slate-500">El usuario se asociara a la sucursal de tu sesion.</p>
      </div>
      <label class="label">Nombre completo
        <input v-model.trim="form.name" class="field mt-1" autocomplete="name" required minlength="2" maxlength="120" />
      </label>
      <label class="label">Correo
        <input v-model.trim="form.email" class="field mt-1" autocomplete="email" type="email" required maxlength="160" />
      </label>
      <label class="label">Rol operativo
        <select v-model="form.role" class="field mt-1" required>
          <option v-for="role in roles" :key="role.value" :value="role.value">{{ role.label }}</option>
        </select>
      </label>
      <label class="label">Contrasena temporal
        <input v-model="form.password" class="field mt-1" autocomplete="new-password" type="password" required minlength="8" />
      </label>
      <p v-if="error" class="rounded-md bg-red-50 p-3 text-sm text-red-700">{{ error }}</p>
      <p v-if="created" class="rounded-md bg-emerald-50 p-3 text-sm text-emerald-700">Usuario {{ created.name }} creado correctamente.</p>
      <button class="btn-primary justify-center" type="submit" :disabled="loading">
        <LoaderCircle v-if="loading" class="h-4 w-4 animate-spin" />
        <UserPlus v-else class="h-4 w-4" />
        {{ loading ? 'Creando...' : 'Crear usuario' }}
      </button>
    </form>
  </section>
</template>

<script setup>
import { computed, reactive, ref } from 'vue'
import { LoaderCircle, UserPlus } from 'lucide-vue-next'
import { apiRequest } from '../services/api'

const props = defineProps({ sessionUser: { type: Object, required: true } })
const isAdministrator = computed(() => props.sessionUser.role === 'ADMINISTRATOR')
const roles = [
  { value: 'ADMINISTRATOR', label: 'Administrador del sistema' },
  { value: 'RECEPTIONIST', label: 'Recepcionista' },
  { value: 'MANAGER', label: 'Gerente' },
  { value: 'SECRETARY', label: 'Secretaria' },
  { value: 'MECHANIC', label: 'Mecanico' },
  { value: 'ACCOUNTANT', label: 'Contador' }
]
const form = reactive({ name: '', email: '', password: '', role: 'RECEPTIONIST' })
const error = ref(''), created = ref(null), loading = ref(false)

async function submit() {
  error.value = ''
  created.value = null
  loading.value = true
  try {
    const session = await apiRequest('/auth/register', { method: 'POST', body: JSON.stringify(form) })
    created.value = session.user
    form.name = ''; form.email = ''; form.password = ''; form.role = 'RECEPTIONIST'
  } catch (err) {
    error.value = err.message || 'No fue posible crear el usuario.'
  } finally {
    loading.value = false
  }
}
</script>
