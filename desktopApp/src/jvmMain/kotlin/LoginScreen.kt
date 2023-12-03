import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.KeyboardArrowLeft
import androidx.compose.material.icons.outlined.KeyboardArrowRight
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Composable
fun loginScreen(toggleLogged: (ActiveScreen) -> Unit) {
    val apiClient = ApiClientLocal.current
    val doLogin: suspend (String, String) -> Boolean = { username: String, password: String ->
        apiClient.login(username, password)
    }
    val onLoginSuccess = { toggleLogged(ActiveScreen.MAIN) }
    Row(horizontalArrangement = Arrangement.Center, modifier = Modifier.fillMaxWidth()) {
        Column(verticalArrangement = Arrangement.Center, modifier = Modifier.fillMaxHeight()) {
            Row(horizontalArrangement = Arrangement.Center) {
                var login by rememberSaveable { mutableStateOf("") }
                var pass by rememberSaveable { mutableStateOf("") }

                Column {
                    OutlinedTextField(
                        login,
                        onValueChange = {
                            login = it
                        },
                        modifier = Modifier.background(Color.Cyan).width(300.dp),
                        placeholder = {
                            Text("Login")
                        }
                    )
                    var showPassword by remember {
                        mutableStateOf(false)
                    }
                    TextField(
                        pass,
                        onValueChange = { text ->
                            pass = text
                        },

                        modifier = Modifier.background(Color.Cyan).width(300.dp),
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

                    Row(horizontalArrangement = Arrangement.SpaceEvenly, modifier = Modifier.width(300.dp)) {
                        Button(
                            onClick = { onLoginButtonClick(login, pass, onLoginSuccess, doLogin) },
                            Modifier.width(125.dp)
                        ) {
                            Text("Login")
                        }
                        Button(onClick = { toggleLogged(ActiveScreen.REGISTRATION) }, Modifier.width(125.dp)) {
                            Text("Registration")
                        }
                    }
                }
            }
        }
    }
}

fun onLoginButtonClick(
    login: String,
    pass: String,
    onSuccess: () -> Unit,
    doLogin: suspend (String, String) -> Boolean
) {

    val scope = CoroutineScope(Dispatchers.Default)
    scope.launch {
        val result = doLogin(login, pass)
        if (result) {
            onSuccess()
        }
    }
}

fun onRegistrationButton() {

}