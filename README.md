# IUG Plugin

**IUG Plugin** Interactive User Guide(IUG) is a feature which helps users learn how to use Galaxy AI features in a step by step manner.

---





## Log
### ✨ Features

- Custom Log for real-time logging
- Supports log levels:
    - `DEBUG`
    - `INFO`
    - `WARN`
    - `ERROR`
- Tag-based logging (like Android's `Log.d(TAG, message)`)
- Filter logs by:
    - **Log level**
    - **Tag**
- UI controls:
    - **Clear logs**
    - **Copy logs to clipboard**

---

### 🛠 Technologies Used

- **Swing / AWT UI Components**
    - `JPanel`, `JTextField`, `JList`, `JScrollPane`, `JComboBox`, etc.
    - Used to build the custom log UI and filtering tools.


- **Plugin DevKit**  

---

### 🧪 How to Use

Use the `Log` class to log messages from anywhere in this plugin:

```kotlin
Log.d("MyPlugin", "Debug message")
Log.i("ADB", "Connection successfully")
Log.w("Stream", "Slow query detected")
Log.e("Crash", "Unhandled exception")
