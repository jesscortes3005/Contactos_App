FROM eclipse-temurin:17

# instalar herramientas necesarias
RUN apt-get update && apt-get install -y \
    wget \
    unzip \
    git \
    && rm -rf /var/lib/apt/lists/*

# variables de entorno del SDK
ENV ANDROID_HOME=/opt/android-sdk
ENV PATH=$PATH:$ANDROID_HOME/cmdline-tools/latest/bin:$ANDROID_HOME/platform-tools

# descargar Android SDK
RUN mkdir -p $ANDROID_HOME/cmdline-tools \
    && cd $ANDROID_HOME/cmdline-tools \
    && wget https://dl.google.com/android/repository/commandlinetools-linux-11076708_latest.zip \
    && unzip commandlinetools-linux-11076708_latest.zip \
    && mv cmdline-tools latest \
    && rm commandlinetools-linux-11076708_latest.zip

# aceptar licencias
RUN yes | sdkmanager --licenses

# instalar plataformas necesarias
RUN sdkmanager "platform-tools" "platforms;android-34" "build-tools;34.0.0"

WORKDIR /app

COPY . .

RUN chmod +x gradlew

RUN ./gradlew assembleDebug

CMD ["./gradlew","tasks"]