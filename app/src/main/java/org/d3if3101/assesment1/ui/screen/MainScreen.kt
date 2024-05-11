package org.d3if3101.assesment1.ui.screen

import android.content.res.Configuration
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import org.d3if3101.assesment1.R
import org.d3if3101.assesment1.database.ToDoDB
import org.d3if3101.assesment1.model.ToDoList
import org.d3if3101.assesment1.navigation.Screen
import org.d3if3101.assesment1.ui.theme.Assesment1Theme
import org.d3if3101.assesment1.util.ViewModelFactory

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(navController: NavHostController)
{
    var showList by remember {
        mutableStateOf(true)
    }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text(text = stringResource(id = R.string.app_name)) }
                , colors = TopAppBarDefaults.mediumTopAppBarColors(
                    //Warna Bar
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    //Warna Title
                    titleContentColor = MaterialTheme.colorScheme.primary
                ),
                actions = { 
                    IconButton(onClick = {showList = !showList}) {
                        Icon(
                            painter = painterResource(
                                if(showList) R.drawable.baseline_grid_view_24
                                else R.drawable.baseline_view_list_24
                            ),
                            contentDescription = stringResource(
                                if(showList) R.string.grid
                                else R.string.list
                            ),
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            )
        },
        floatingActionButton =
        {
            FloatingActionButton(onClick = {navController.navigate(Screen.FormBaru.route)})
            {
                Icon(imageVector = Icons.Filled.Add,
                    contentDescription = stringResource(id = R.string.tambah_todolist),
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }
    ) {padding ->
        ScreenContent(showList,Modifier.padding(padding), navController)
    }
}

@Composable
fun ScreenContent(showList: Boolean, modifier: Modifier, navController: NavHostController)
{
    val context = LocalContext.current
    val db = ToDoDB.getInstance(context)
    val factory = ViewModelFactory(db.dao)

    val  viewModel : MainViewModel = viewModel(factory = factory)
    val data by viewModel.data.collectAsState()

    if(data.isEmpty())
    {
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        )
        {
            Text(text = stringResource(id = R.string.list_kosong))
        }
    }
    else
    {
        if (showList){
            LazyColumn (modifier = modifier
                .fillMaxSize(),
                contentPadding = PaddingValues(bottom = 84.dp)
            )
            {
                items(data)
                {
                    ListItem(toDoList = it)
                    {
                        navController.navigate(Screen.FormUbah.withId(it.id))
                    }
                    Divider()
                }
            }
        } else{
            LazyVerticalStaggeredGrid(
                modifier = modifier.fillMaxSize(),
                columns = StaggeredGridCells.Fixed(2),
                verticalItemSpacing = 8.dp,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(8.dp, 8.dp, 8.dp, 84.dp)
            ){
                items(data){
                   GridItem(toDoList = it) {
                       navController.navigate(Screen.FormUbah.withId(it.id))
                   }
                }
            }
        }

    }

}

@Composable
fun ListItem(toDoList: ToDoList,
             onClick: () -> Unit
)
{
    Column(modifier = Modifier
        .fillMaxWidth()
        .clickable { onClick() }
        .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp))
    {
        if(toDoList.status == "Selesai"){

            Text(text = toDoList.judul,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                fontWeight = FontWeight.Bold,
            )
            Text(text = toDoList.isi,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.blur(3.dp)
            )
            Text(
                text = toDoList.tanggal,
                modifier = Modifier.blur(3.dp)
            )
        }else{
            Text(text = toDoList.judul,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                fontWeight = FontWeight.Bold
            )
            Text(text = toDoList.isi,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            Text(text = toDoList.tanggal)
        }

    }
}

@Composable
fun GridItem(toDoList: ToDoList, onClick: () -> Unit){
    Card (
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
        ),
        border = BorderStroke(1.dp, Color.Gray)
    ){
        Column(
            modifier = Modifier
                .padding(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        )
        {
            if(toDoList.status == "Selesai"){

                Text(text = toDoList.judul,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    fontWeight = FontWeight.Bold,
                )
                Text(text = toDoList.isi,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.blur(3.dp)
                )
                Text(
                    text = toDoList.tanggal,
                    modifier = Modifier.blur(3.dp)
                )
            }else{
                Text(text = toDoList.judul,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    fontWeight = FontWeight.Bold
                )
                Text(text = toDoList.isi,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Text(text = toDoList.tanggal)
            }

        }
    }
}

@Preview(showBackground = true)
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES, showBackground = true)
@Composable
fun GreetingPreview() {
    Assesment1Theme {
        MainScreen(rememberNavController())
    }
}