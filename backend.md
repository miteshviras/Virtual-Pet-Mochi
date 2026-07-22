# Backend API Specification (Offline Game)

## Overview
This application operates 100% offline on Android devices. No external backend servers, remote APIs, cloud databases, or authentication endpoints are required. All pet state, inventory data, mini-game high scores, and customization settings are persisted locally on device using secure encrypted local storage / Room database and Unity binary/JSON local saves.

## Local Endpoints & Persistence Contract
- **Offline Local Save File**: `mochi_save_data.dat` / SQLite database (`mochi_pet.db`)
- **Network Requests**: NONE (0 network endpoints used).
