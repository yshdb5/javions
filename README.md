# Javions - Real-Time Aircraft Tracking System

Javions is a sophisticated real-time aircraft tracking application that receives, decodes, and visualizes ADS-B (Automatic Dependent Surveillance-Broadcast) messages from aircraft. The system provides live tracking of aircraft positions, velocities, and identification data with an interactive map interface.

## 🚀 Features

### Core Functionality
- **Real-time ADS-B Demodulation**: Process live radio signals from RTL-SDR or similar hardware
- **Message Parsing**: Decode aircraft identification, position, and velocity messages
- **Interactive Map**: OpenStreetMap-based visualization with aircraft overlay
- **Aircraft Database**: Comprehensive aircraft information with registration details
- **Live Tracking Table**: Real-time aircraft state monitoring
- **Historical Playback**: Replay recorded ADS-B message files

### Technical Highlights
- **Concurrent Processing**: Multi-threaded message handling and GUI updates
- **CPR Decoding**: Compact Position Reporting for precise aircraft positioning
- **Signal Processing**: Advanced demodulation with power computation algorithms
- **Tile Caching**: Efficient map tile management and caching
- **State Management**: Automatic aircraft state accumulation and cleanup

## 📦 Installation

### Prerequisites
1. Ensure Java 17+ is installed:
   ```bash
   java -version
   ```

2. Verify JavaFX 21 is available in your system or IDE

### Setup
1. **Clone or download** the project directory

```
    git clone git@github.com:yshdb5/Javions.git
```


2. **Set up your IDE** (IntelliJ IDEA recommended):
   - Ensure JavaFX 21 library is configured
   - Verify JUnit 5.8.1 is available for testing

### Interface Guide

#### Main Window Components
1. **Map View** (Left Panel):
   - Interactive OpenStreetMap with aircraft icons
   - Zoom and pan capabilities
   - Aircraft trails showing flight paths
   - Click aircraft icons for detailed information

2. **Aircraft Table** (Right Panel):
   - Real-time list of detected aircraft
   - ICAO address, call sign, registration
   - Position, altitude, and velocity data
   - Double-click to center map on aircraft

3. **Status Bar** (Bottom):
   - Aircraft count
   - Message processing statistics
   - Connection status

#### Aircraft Information
- **ICAO Address**: Unique 24-bit aircraft identifier
- **Call Sign**: Flight identification (e.g., "SWR123")
- **Registration**: Aircraft registration number
- **Position**: Latitude/longitude coordinates
- **Altitude**: Barometric altitude in feet
- **Velocity**: Ground speed and heading
- **Squawk**: Transponder code

## 🏗️ Project Structure

```
Javions/
├── src/ch/epfl/javions/
│   ├── adsb/              # ADS-B message parsing
│   │   ├── Message.java
│   │   ├── MessageParser.java
│   │   ├── AirbornePositionMessage.java
│   │   ├── AirborneVelocityMessage.java
│   │   └── AircraftIdentificationMessage.java
│   ├── aircraft/          # Aircraft database management
│   │   ├── AircraftDatabase.java
│   │   ├── AircraftData.java
│   │   └── IcaoAddress.java
│   ├── demodulation/      # Signal processing
│   │   ├── AdsbDemodulator.java
│   │   ├── PowerComputer.java
│   │   └── SamplesDecoder.java
│   ├── gui/              # User interface
│   │   ├── Main.java
│   │   ├── AircraftController.java
│   │   ├── AircraftTableController.java
│   │   └── BaseMapController.java
│   └── [utility classes]
├── test/                 # Comprehensive test suite
├── resources/
│   ├── aircraft.zip     # Aircraft database
│   ├── *.css           # UI styling
│   └── sample data files
├── lib/                 # JUnit dependencies
└── tile-cache/         # Map tile cache
```

## 🏫 Academic Context

This project was developed as part of the Object-Oriented Programming course (PPOO) at **EPFL** (École Polytechnique Fédérale de Lausanne). It demonstrates advanced Java programming concepts including:

- Object-oriented design patterns
- Concurrent programming
- Signal processing algorithms
- GUI development with JavaFX
- Test-driven development
- Domain-specific problem solving

## 👥 Authors

- **Yshai** (356356)
- **Gabriel Taieb** (360560)

*Developed during Spring 2023 semester at EPFL*

## 📄 License

This project is open source and available under the [MIT License](LICENSE).
