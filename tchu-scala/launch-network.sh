#!/bin/bash
# Launch tCHu in network mode with two terminals

# Get the directory where this script is located
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"

echo "Starting tCHu in network mode..."
echo "Opening server terminal..."

# Start server in a new terminal
if command -v gnome-terminal &> /dev/null; then
    gnome-terminal --title="tCHu Server" -- bash -c "cd '$SCRIPT_DIR' && sbt run; read -p 'Press Enter to close...'"
    sleep 2
    gnome-terminal --title="tCHu Client" -- bash -c "cd '$SCRIPT_DIR' && sbt 'run localhost'; read -p 'Press Enter to close...'"
elif command -v xterm &> /dev/null; then
    xterm -title "tCHu Server" -e "cd '$SCRIPT_DIR' && sbt run; read -p 'Press Enter to close...'" &
    sleep 2
    xterm -title "tCHu Client" -e "cd '$SCRIPT_DIR' && sbt 'run localhost'; read -p 'Press Enter to close...'" &
elif command -v konsole &> /dev/null; then
    konsole --new-tab -e bash -c "cd '$SCRIPT_DIR' && sbt run; read -p 'Press Enter to close...'" &
    sleep 2
    konsole --new-tab -e bash -c "cd '$SCRIPT_DIR' && sbt 'run localhost'; read -p 'Press Enter to close...'" &
else
    echo "No supported terminal emulator found (gnome-terminal, xterm, konsole)"
    echo "Please run manually:"
    echo "  Terminal 1: sbt run"
    echo "  Terminal 2: sbt 'run localhost'"
    exit 1
fi

echo "Both terminals started!"
