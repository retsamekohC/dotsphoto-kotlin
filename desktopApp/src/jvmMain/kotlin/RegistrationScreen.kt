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

@Composable
fun registrationScreen(changeScreen: (ActiveScreen) -> Unit) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
        Column(modifier = Modifier.fillMaxHeight(), verticalArrangement = Arrangement.Center) {
            Row(horizontalArrangement = Arrangement.Center, modifier = Modifier.width(280.dp)) {
                Text("Welcome", fontSize = TextUnit(2f, TextUnitType.Em), color = Color(110, 20, 239))
            }
            var firstName by remember { mutableStateOf("") }
            var lastName by remember { mutableStateOf("") }
            var patronymic by remember { mutableStateOf("") }
            var email by remember { mutableStateOf("") }
            var pass by remember { mutableStateOf("") }
            TextField(
                firstName, onValueChange = {
                    firstName = it
                },
                modifier = Modifier.background(Color.Cyan),
                placeholder = { Text("Egor") }
            )
            TextField(
                lastName, onValueChange = {
                    lastName = it
                },
                modifier = Modifier.background(Color.Cyan),
                placeholder = { Text("Egorov") }
            )
            TextField(
                patronymic, onValueChange = {
                    patronymic = it
                },
                modifier = Modifier.background(Color.Cyan),
                placeholder = { Text("Egorovich") }
            )
            TextField(
                email, onValueChange = {
                    email = it
                },
                modifier = Modifier.background(Color.Cyan),
                placeholder = { Text("name@example.com") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
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
                        changeScreen,
                        RegistrationData(firstName, lastName, patronymic, email, pass)
                    )
                }) {
                    Text("Sign Up")
                }
                Button(onClick = { changeScreen(ActiveScreen.LOGIN) }) {
                    Text("Sign In")
                }
            }
        }
    }
}


@Suppress("unused")
class RegistrationData(
    var firstName: String,
    var lastName: String,
    var patronymics: String,
    var email: String,
    var pass: String
)

@Suppress("UNUSED_PARAMETER")
fun registration(changeScreen: (ActiveScreen) -> Unit, regData: RegistrationData) {
    changeScreen(ActiveScreen.LOGIN)
}


