# Weather Tracker

A modern Android weather application built with Jetpack Compose that allows users to search for cities and view their weather details.

## Features

- Search for cities worldwide
- View detailed weather information including:
    - Temperature
    - Humidity
    - UV Index
    - Feels Like temperature
- Offline support with local caching
- Clean and modern Material Design UI
- Real-time weather updates

## Screenshots

[Add your app screenshots here]

## Tech Stack

- **Language**: Kotlin
- **UI Framework**: Jetpack Compose
- **Architecture**: MVVM
- **Dependency Injection**: Hilt
- **Database**: Room
- **Networking**: Retrofit
- **API**: WeatherAPI.com
- **Other Libraries**:
    - Coroutines for asynchronous operations
    - Flow for reactive programming
    - Coil for image loading

## Setup Instructions

1. Clone the repository
   git clone https://github.com/SiddharthSemani/Weather_Tracker.git

2. Get an API key from [WeatherAPI.com](https://www.weatherapi.com/)

3. Create a `local.properties` file in the root directory and add: WEATHER_API_KEY=your_api_key_here

4. Open the project in Android Studio

5. Sync project with Gradle files

6. Run the app on an emulator or physical device

## Project Structure

com.example.weathertracker/
├── data/
│ ├── local/ # Room database
│ ├── remote/ # API services
│ └── repository/ # Repository implementation
├── di/ # Dependency injection modules
├── domain/
│ └── model/ # Domain models
├── ui/
│ ├── theme/ # App theme and styling
│ └── # App screens
├── utils/ # Utility classes
|
└── viewModel

## Requirements

- Android Studio Arctic Fox or later
- Android SDK 24 or higher
- Kotlin 1.8.0 or higher

## Contributing

1. Fork the repository
2. Create your feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add some amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

## License

[Add your license information here]

## Acknowledgments

- Weather data provided by [WeatherAPI.com](https://www.weatherapi.com/)
- Icons from [Material Design Icons](https://material.io/resources/icons/)