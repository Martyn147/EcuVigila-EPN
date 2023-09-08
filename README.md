# Proyecto de Tracking en Tiempo Real y segundo plano

Este proyecto de Android tiene como objetivo rastrear la ubicación del usuario en tiempo real utilizando un servicio en segundo plano y almacenar los datos de ubicación en Firebase Realtime Database. El servicio en segundo plano garantiza que la aplicación continúe rastreando la ubicación incluso cuando se minimiza o se ejecuta en segundo plano.

## Características

- Rastreo de ubicación en tiempo real.
- Almacenamiento de datos de ubicación en Firebase Realtime Database.
- CRUD de usuarios con perfiles

## Requisitos

- Android Studio Giraffe.
- Conexión a Internet para almacenar datos en Firebase.
- Cuenta de Firebase para configurar el proyecto y obtener las credenciales necesarias.
- Api key de google maps.

## Configuración

1. Clona o descarga este repositorio en tu máquina local.
2. Abre el proyecto en Android Studio.
3. Configura tu proyecto en Firebase, agrega la apikey y cuenta de firebase.
4. Asegúrate de configurar las reglas de Firebase Realtime Database para permitir el acceso a los datos de ubicación.

## Uso

1. Ejecuta la aplicación en tu dispositivo Android.
2. Concede los permisos de ubicación necesarios cuando se te solicite.
3. La aplicación comenzará a rastrear tu ubicación en tiempo real y almacenará los datos en Firebase.
4. Al apagar la pantalla o minimizar la app el servicio segira gurdando tu ubicacion.

## Logica de programacion

1. La app tiene un perfil administrador que puede eliminar o crear usuarios que puedan accder.
2. Con una cuenta normal se tiene acceso al mapa.
3. Puedes verte a ti mismo y a los demas usuarios conectados.
4. Podras ver la informacion de cada usuario dando un tap en su pin.
