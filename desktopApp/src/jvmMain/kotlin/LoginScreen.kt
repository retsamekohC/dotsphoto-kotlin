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
import androidx.compose.ui.Alignment
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
fun loginScreen(goToMain: () -> Unit, goToRegistration: () -> Unit) {
    val apiClient = ApiClientLocal.current
    val doLogin: suspend (String, String) -> Boolean = { username: String, password: String ->
        apiClient.login(username, password)
    }
    val onLoginSuccess = { goToMain() }
    Row(horizontalArrangement = Arrangement.Center, modifier = Modifier.fillMaxWidth().background(Color(200,255,255))) {
        Column(verticalArrangement = Arrangement.Center, modifier = Modifier.fillMaxHeight()) {
            Row(horizontalArrangement = Arrangement.Center) {
                var login by rememberSaveable { mutableStateOf("") }
                var pass by rememberSaveable { mutableStateOf("") }

                Column(Modifier.fillMaxHeight(), verticalArrangement = Arrangement.Center) {
                    Row(horizontalArrangement = Arrangement.Center, modifier = Modifier.width(300.dp)) {
                        Text("Login", fontSize = TextUnit(2f, TextUnitType.Em), color = Color(110, 20, 239))
                    }
                    TextField(
                        login,
                        onValueChange = {
                            login = it
                        },
                        modifier = Modifier.background(Color(75, 255, 255)).width(300.dp),
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

                        modifier = Modifier.background(Color(75, 255, 255)).width(300.dp),
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

                    Row(horizontalArrangement = Arrangement.SpaceEvenly, modifier = Modifier.width(300.dp).height(60.dp), verticalAlignment = Alignment.CenterVertically) {
                        Button(
                            onClick = { onLoginButtonClick(login, pass, onLoginSuccess, doLogin) },
                            Modifier.width(135.dp).height(40.dp)
                        ) {
                            Text("Login",fontSize = TextUnit(1f, TextUnitType.Em))
                        }
                        Button(onClick = goToRegistration , Modifier.width(135.dp).height(40.dp)) {
                            Text("Registration",fontSize = TextUnit(1f, TextUnitType.Em))
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