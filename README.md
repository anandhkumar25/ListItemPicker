**What is this library?**

Overview

ListItemPicker UI Library is a lightweight and efficient UI component library built using Jetpack Compose for Android. This library features an Item Picker View that includes sound and haptic feedback, creating an enhanced, interactive experience for users. Perfect for applications that require intuitive item selection, this component is easy to integrate and customize.

---

**Features**

- Item Picker View: A simple, user-friendly component for selecting items from a list.
  
- Sound Feedback: Provides auditory feedback when items are selected, improving the user experience.
  
- Haptic Feedback: Delivers vibration feedback, making interactions more engaging and tactile.
  
- Jetpack Compose: Fully built using Jetpack Compose, ensuring compatibility with modern Android development practices.
  
- Customizable: Easily style the picker to fit your app’s design system.

---

**How to Install?**

- Open your Project's settings.gradle file.

- Add the following...

```
dependencyResolutionManagement {
    ...
    repositories {
        google()
        mavenCentral()
        maven {
            url = uri("https://jitpack.io") // Add this
        }
    }
}
```

- Go to build.gradle of app module
- Add the following dependency:

```
dependencies {
    ...
    implementation 'com.github.anandhkumar25:ListItemPicker:1.0.1') // Add this with latest version
}
```
Enjoy using ListPickerItem components

---

**Example**
```
package com.anandh.listitempicker

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import com.anandh.listitempicker.ui.theme.ListItemPickerTheme

class MainActivity : ComponentActivity() {

    val items = listOf("Item 1", "Item 2", "Item 3", "Item 4", "Item 5", "Item 6", "Item 7", "Item 8")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            var selectedItem by remember { mutableStateOf(items.first()) }
            ListItemPickerTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    ListItemPicker(
                        modifier = Modifier.padding(innerPadding),
                        itemFormatter = { it },
                        selectedItem = selectedItem,
                        onSelectionChange = { selectedItem = it },
                        separatorColor = Color.Gray,
                        items = items,
                        itemTextStyle = TextStyle(color = Color.White),
                        enableSound = true,
                        enableHaptic = true
                    )
                }
            }
        }
    }
}
```

---

**Output**

Here’s a demo showcasing the Item Picker View in action with sound and haptic feedback:

https://github.com/user-attachments/assets/881efa5e-d1e5-4080-a296-34978b65b624

---


