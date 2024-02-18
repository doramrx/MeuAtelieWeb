export function normalizePhone(phone: string): string {
  return phone.replace(/\D+/g, '');
}
