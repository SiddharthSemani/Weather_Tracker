package com.example.weathertracker.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Card
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.myapplication.ui.theme.Background
import com.example.myapplication.ui.theme.TextPrimary
import com.example.myapplication.ui.theme.TextSecondary
import com.example.myapplication.ui.theme.TextTertiary
import com.example.weathertracker.R
import com.example.weathertracker.data.remote.WeatherResponse
import com.example.weathertracker.domain.model.RefinedViewState
import com.example.weathertracker.domain.model.WeatherContract
import com.example.weathertracker.viewModel.WeatherViewModel
import kotlin.math.roundToInt

@Composable
fun WeatherScreen(
    viewModel: WeatherViewModel,
    onSearch: (String) -> Unit,
    showToast: (String) -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()

    LaunchedEffect(viewModel) {
        viewModel.actions.collect { action ->
            when (action) {
                is WeatherContract.Action.ShowToast -> {
                    showToast(action.message)
                }
                is WeatherContract.Action.SaveCity -> {}
            }
        }
    }


    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(16.dp)
    ) {
        Spacer(modifier = Modifier.height(44.dp))
        SearchBar(
            query = searchQuery,
            onQueryChange = viewModel::onSearchQueryChange,
            onSearch = onSearch,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(24.dp))
        when (val state = uiState.refinedViewState) {
            is RefinedViewState.Loading -> LoadingState()
            is RefinedViewState.SearchResult -> SearchResultCard(
                weather = state.weather,
                onSelect = viewModel::selectCity
            )

            is RefinedViewState.WeatherDetail -> WeatherDetailState(weather = state.weather)
            is RefinedViewState.Empty -> EmptyState()
            is RefinedViewState.Error -> ErrorState()
        }
    }
}


@Composable
private fun SearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    onSearch: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    TextField(
        value = query,
        onValueChange = onQueryChange,
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Background),
        placeholder = {
            Text(
                text = stringResource(R.string.search_hint),
                style = TextStyle(
                    fontFamily = FontFamily(Font(R.font.poppins_regular)),
                    lineHeight = 22.5.sp,
                    fontSize = 15.sp,
                    color = TextSecondary
                )
            )
        },
        trailingIcon = {
            IconButton(onClick = { onSearch(query) }) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_search),
                    contentDescription = stringResource(R.string.search_icon),
                    modifier = Modifier.size(18.dp)
                )
            }
        },
        colors = TextFieldDefaults.textFieldColors(
            backgroundColor = Color.Transparent,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            cursorColor = TextPrimary,
            textColor = TextPrimary
        ),
        textStyle = TextStyle(
            fontFamily = FontFamily(Font(R.font.poppins_regular)),
            fontSize = 15.sp
        ),
        singleLine = true,
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
        keyboardActions = KeyboardActions(onSearch = { onSearch(query) })
    )
}

@Composable
private fun WeatherDetailState(weather: WeatherResponse) {
    Column(
        modifier = Modifier.fillMaxWidth().padding(top = 50.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        AsyncImage(
            model = "https:${weather.current.condition.icon}",
            contentDescription = weather.current.condition.text,
            modifier = Modifier
                .width(123.dp)
                .height(123.dp)
        )
        Spacer(modifier = Modifier.height(24.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = weather.location.name,
                style = TextStyle(
                    fontFamily = FontFamily(Font(R.font.poppins_semibold)),
                    lineHeight = 45.sp,
                    fontSize = 30.sp,
                    color = TextPrimary
                )
            )
            Icon(
                painter = painterResource(id = R.drawable.ic_location),
                contentDescription = null,
                modifier = Modifier.padding(start = 11.dp)
            )
        }
        Row(
            verticalAlignment = Alignment.Top,
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                text = weather.current.tempC.roundToInt().toString(),
                style = TextStyle(
                    fontFamily = FontFamily(Font(R.font.poppins_medium)),
                    lineHeight = 105.sp,
                    fontSize = 70.sp,
                    color = TextPrimary
                )
            )
            Icon(
                painter = painterResource(id = R.drawable.ic_degree_large),
                contentDescription = "degree",
                modifier = Modifier.padding(top = 8.dp)
            )
        }
        Spacer(modifier = Modifier.height(35.dp))
        WeatherMetricsCard(weather)
    }
}

@Composable
private fun MetricItem(
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = label,
            style = TextStyle(
                fontFamily = FontFamily(Font(R.font.poppins_medium)),
                lineHeight = 18.sp,
                fontSize = 12.sp,
                color = TextSecondary,
                textAlign = TextAlign.Center
            )
        )
        Text(
            text = value,
            style = TextStyle(
                fontFamily = FontFamily(Font(R.font.poppins_medium)),
                lineHeight = 22.5.sp,
                fontSize = 15.sp,
                color = TextTertiary,
                textAlign = TextAlign.Center
            ),
            modifier = Modifier.padding(top = 2.dp)
        )
    }
}

@Composable
private fun EmptyState() {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = stringResource(R.string.empty_state_title),
            style = TextStyle(
                fontFamily = FontFamily(Font(R.font.poppins_semibold)),
                lineHeight = 45.sp,
                fontSize = 30.sp,
                color = TextPrimary
            )
        )
        Spacer(modifier = Modifier.height(24.dp))
        Text(
            text = stringResource(R.string.empty_state_subtitle),
            style = TextStyle(
                fontFamily = FontFamily(Font(R.font.poppins_regular)),
                lineHeight = 22.5.sp,
                fontSize = 15.sp,
                color = TextPrimary
            )
        )
    }
}

@Composable
private fun ErrorState() {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = stringResource(R.string.error_state_title),
            style = TextStyle(
                fontFamily = FontFamily(Font(R.font.poppins_semibold)),
                lineHeight = 45.sp,
                fontSize = 30.sp,
                color = TextPrimary
            )
        )
        Spacer(modifier = Modifier.height(24.dp))
        Text(
            text = stringResource(R.string.error_state_subtitle),
            style = TextStyle(
                fontFamily = FontFamily(Font(R.font.poppins_regular)),
                lineHeight = 22.5.sp,
                fontSize = 15.sp,
                color = TextPrimary
            )
        )
    }
}


@Composable
private fun WeatherMetricsCard(weather: WeatherResponse) {
    Card(
        backgroundColor = Background,
        elevation = 0.dp,
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            MetricItem(
                label = stringResource(R.string.humidity_label),
                value = stringResource(R.string.humidity_format, weather.current.humidity)
            )
            MetricItem(
                label = stringResource(R.string.uv_label),
                value = weather.current.uv.toString(),
                modifier = Modifier.padding(horizontal = 56.dp)
            )
            MetricItem(
                label = stringResource(R.string.feels_like_label),
                value = stringResource(
                    R.string.temperature_format,
                    weather.current.feelsLikeC.roundToInt()
                )
            )
        }
    }
}


@Composable
private fun LoadingState() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(
            color = MaterialTheme.colors.primary
        )
    }
}


@Composable
private fun SearchResultCard(
    weather: WeatherResponse,
    onSelect: (WeatherResponse) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onSelect(weather) },
        backgroundColor = Background,
        elevation = 0.dp,
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(horizontal = 31.dp)
                .padding(top = 16.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceEvenly,
            ) {
                Text(
                    text = weather.location.name,
                    style = TextStyle(
                        fontFamily = FontFamily(Font(R.font.poppins_semibold)),
                        fontSize = 20.sp,
                        color = TextPrimary
                    )
                )
                Row(
                    verticalAlignment = Alignment.Top,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "${weather.current.tempC.roundToInt()}",
                        style = TextStyle(
                            fontFamily = FontFamily(Font(R.font.poppins_medium)),
                            fontSize = 60.sp,
                            color = TextPrimary
                        )
                    )
                    Text(
                        text = stringResource(R.string.degree_symbol),
                        style = TextStyle(
                            fontFamily = FontFamily(Font(R.font.poppins_medium)),
                            color = TextPrimary,
                            fontSize = 20.sp
                        ),
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }
            }
            AsyncImage(
                model = "https:${weather.current.condition.icon}",
                contentDescription = weather.current.condition.text,
                modifier = Modifier
                    .padding(bottom = 16.dp)
                    .width(83.dp),
                contentScale = ContentScale.FillWidth
            )
        }
    }
}



