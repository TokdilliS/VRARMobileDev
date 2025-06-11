import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.mobileanwendungvorlesung.Screen

// In BottomNavigationBar.kt (oder in MainActivity.kt, wo die BottomNavigationBar definiert ist)
// ...
@Composable
fun BottomNavigationBar(navController: NavController) {
    // HIER die Anpassung: Füge Screen.QRScanner hinzu, wo vorher AddContact war (falls zutreffend)
    val items = listOf(Screen.ContactList, Screen.QRScanner, Screen.Settings)
    val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route

    NavigationBar {
        items.forEach { screen ->
            NavigationBarItem(
                selected = currentRoute == screen.route,
                onClick = {
                    if (currentRoute != screen.route) {
                        navController.navigate(screen.route) {
                            popUpTo(navController.graph.startDestinationRoute ?: "") {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                },
                label = { Text(screen.titel) },
                icon = {Icon(screen.icon, contentDescription = null)}
            )
        }
    }
}
// ...