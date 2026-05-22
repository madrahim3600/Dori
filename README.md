# 💊 Dori Eslatma

Dori ichishni eslatuvchi Android ilovasi. **To'liq offline ishlaydi** — internet yoki server kerak emas. Barcha ma'lumotlar telefon xotirasida (Room ma'lumotlar bazasi) saqlanadi.

## Imkoniyatlar

- ✅ Dori qo'shish: nomi, dozasi, ovqatga nisbatan (oldin/keyin/orasida)
- ✅ Necha mahal, qaysi vaqtlarda ichish (masalan 08:00, 14:00, 20:00)
- ✅ Har qabulda nechta dona, necha kun davom etishi
- ✅ Belgilangan vaqtlarda **ovozli bildirishnoma** (telefon uxlab yotsa ham ishlaydi)
- ✅ Dorilar ro'yxati va **qancha zaxira qolgani** progress bilan ko'rinadi
- ✅ Zaxira tugayotganda ogohlantirish
- ✅ Dorini tahrirlash, o'chirish, eslatmani vaqtincha o'chirish/yoqish
- ✅ Telefon qayta yonganda eslatmalar avtomatik tiklanadi
- ✅ Zamonaviy o'zbekcha interfeys

## Texnologiyalar

- Kotlin + Jetpack Compose (Material 3)
- Room (lokal ma'lumotlar bazasi)
- AlarmManager (aniq vaqtli eslatmalar)
- minSdk 24 (Android 7.0+), targetSdk 34

## APK ni qanday olish (GitHub Actions orqali)

1. Loyihani GitHub'ga yuklang (pastdagi ko'rsatmaga qarang).
2. GitHub repo sahifasida **Actions** bo'limiga o'ting.
3. "APK Build" workflow avtomatik ishga tushadi (yoki **Run workflow** tugmasini bosing).
4. Build tugagach, sahifaning pastida **Artifacts** bo'limidan:
   - `DoriEslatma-debug` — sinov uchun (imzosiz, to'g'ridan-to'g'ri o'rnatiladi)
   - `DoriEslatma-release` — chiqarish versiyasi
5. ZIP ni yuklab oling, ichidan `.apk` faylni telefonga o'rnating.

> **Eslatma:** Debug APK ni o'rnatishda telefonda "Noma'lum manbalardan o'rnatish"ga ruxsat berish kerak bo'ladi.

## GitHub'ga yuklash

```bash
cd dori-eslatma
git init
git add .
git commit -m "Dori Eslatma ilovasi"
git branch -M main
git remote add origin https://github.com/FOYDALANUVCHI/REPO-NOMI.git
git push -u origin main
```

`FOYDALANUVCHI` va `REPO-NOMI` ni o'zingizniki bilan almashtiring.

## Muhim ruxsatlar

Ilova birinchi ochilganda quyidagilarni so'raydi:
- **Bildirishnomalar** (Android 13+) — eslatma chiqishi uchun
- **Aniq vaqtli alarm** (Android 12+) — vaqtida eslatishi uchun

Ikkalasiga ham ruxsat bering, aks holda eslatmalar ishlamaydi.
