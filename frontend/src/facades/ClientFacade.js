import { ClientRepository } from '../repositories/ClientRepository'

const MAX_PHOTO_BYTES = 15 * 1024 * 1024
const allowedTypes = { 'image/jpeg': ['jpg', 'jpeg'], 'image/png': ['png'], 'image/webp': ['webp'] }

export class ClientFacade {
  constructor(repository = new ClientRepository()) { this.repository = repository }
  async validatePhoto(file) {
    if (!file) throw new Error('Selecciona la fotografia del cliente.')
    if (file.size > MAX_PHOTO_BYTES) throw new Error('La fotografia no puede superar 15 MB.')
    const extension = file.name.split('.').pop()?.toLowerCase()
    if (!allowedTypes[file.type]?.includes(extension)) throw new Error('Usa una imagen JPG, JPEG, PNG o WEBP con extension y MIME coincidentes.')
    const bytes = new Uint8Array(await file.slice(0, 12).arrayBuffer())
    const realMime = this.detectMime(bytes)
    if (!realMime || realMime !== file.type) throw new Error('El contenido real de la fotografia no coincide con su tipo MIME.')
    return URL.createObjectURL(file)
  }
  detectMime(bytes) {
    if (bytes.length >= 3 && bytes[0] === 0xff && bytes[1] === 0xd8 && bytes[2] === 0xff) return 'image/jpeg'
    if (bytes.length >= 8 && bytes[0] === 0x89 && bytes[1] === 0x50 && bytes[2] === 0x4e && bytes[3] === 0x47) return 'image/png'
    if (bytes.length >= 12 && bytes[0] === 0x52 && bytes[1] === 0x49 && bytes[2] === 0x46 && bytes[3] === 0x46 && bytes[8] === 0x57 && bytes[9] === 0x45 && bytes[10] === 0x42 && bytes[11] === 0x50) return 'image/webp'
    return null
  }
  validateContext(user) {
    if (!user || !['ADMINISTRATOR', 'RECEPTIONIST'].includes(user.role) || !user.branchId) throw new Error('Tu sesion no tiene permisos para registrar clientes.')
  }
  async register(user, form, photo) {
    this.validateContext(user)
    await this.validatePhoto(photo)
    const data = new FormData()
    Object.entries(form).forEach(([key, value]) => data.append(key, value ?? ''))
    data.append('photo', photo)
    return this.repository.create(data)
  }
}
