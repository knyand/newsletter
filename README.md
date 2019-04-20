# Сервер новостей

Реализация издателя новостей. Реализация подписчика находится [здесь](https://github.com/z-ank/news-subscriber).

## Быстрый запуск

Для запуска выполняем команду `./gradlew run --args='port'` с передачей номера порта, например:
`./gradlew run --args=5678`

## Сборка

Для сборки проекта выполняем команду `./gradlew build`. Артефакт находится в каталоге `build/libs`. Запуск выполняется командой `java -jar build/libs/newsletter-... .jar port`, где port - номер порта. Например `java -jar build/libs/newsletter-1.0.jar 5678`
