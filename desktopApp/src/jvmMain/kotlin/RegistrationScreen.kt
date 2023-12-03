import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.KeyboardArrowLeft
import androidx.compose.material.icons.outlined.KeyboardArrowRight
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Composable
fun registrationScreen(goToLogin: () -> Unit) {
    val apiClient = ApiClientLocal.current
    val doRegistration: suspend (String, String) -> Boolean = { username:String, password:String ->
        apiClient.register(username, password)
    }

    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
        Column(modifier = Modifier.fillMaxHeight(), verticalArrangement = Arrangement.Center) {
            Row(horizontalArrangement = Arrangement.Center, modifier = Modifier.width(280.dp)) {
                Text("Welcome", fontSize = TextUnit(2f, TextUnitType.Em), color = Color(110, 20, 239))
            }
            var username by remember { mutableStateOf("") }
            var pass by remember { mutableStateOf("") }
            TextField(
                username, onValueChange = {
                    username = it
                },
                modifier = Modifier.background(Color.Cyan),
                placeholder = { Text("Egor") }
            )
            var showPassword by remember {
                mutableStateOf(false)
            }
            TextField(
                pass,
                onValueChange = { text ->
                    pass = text
                },

                modifier = Modifier.background(Color.Cyan),
                placeholder = { Text("Enter new password") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                visualTransformation = if (showPassword) VisualTransformation.None else PasswordVisualTransformation(),
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Outlined.Lock,
                        contentDescription = "Lock Icon"
                    )
                },
                trailingIcon = {
                    IconButton(onClick = { showPassword = !showPassword }) {
                        Icon(
                            imageVector = if (showPassword) Icons.Outlined.KeyboardArrowRight else Icons.Outlined.KeyboardArrowLeft,
                            contentDescription = if (showPassword) "Show Password" else "Hide Password"
                        )
                    }
                },
            )

            Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.width(280.dp)) {
                Button(onClick = {
                    registration(
                        goToLogin,
                        doRegistration,
                        RegistrationData(username, pass)
                    )
                }) {
                    Text("Sign Up")
                }
                Button(onClick = { goToLogin() }) {
                    Text("Sign In")
                }
            }
        }
    }
}

class RegistrationData(
    var username: String,
    var pass: String
)

fun registration(goToLogin: () -> Unit, doRegistration: suspend (String, String) -> Boolean, regData: RegistrationData) {
    val scope = CoroutineScope(Dispatchers.Default)
    scope.launch {
        val result = doRegistration(regData.username, regData.pass)
        if (result) {
            goToLogin()
        }
    }
}


