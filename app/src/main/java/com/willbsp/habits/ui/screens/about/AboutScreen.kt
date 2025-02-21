package com.willbsp.habits.ui.screens.about

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.twotone.BugReport
import androidx.compose.material.icons.twotone.DataObject
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.willbsp.habits.BuildConfig
import com.willbsp.habits.R
import com.willbsp.habits.ui.common.DefaultHabitsAppTopBar

@Composable
fun AboutScreen(
    navigateUp: () -> Unit,
) {
    Scaffold(
        topBar = {
            DefaultHabitsAppTopBar(
                title = stringResource(R.string.about_title),
                canNavigateBack = true,
                navigateUp = navigateUp,
            )
        }
    ) { innerPadding ->

        val uriHandler = LocalUriHandler.current

        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            //verticalArrangement = Arrangement.Center
        ) {

            Icon(
                modifier = Modifier.size(100.dp),
                painter = painterResource(id = R.drawable.ic_launcher_foreground),
                contentDescription = "",
                tint = MaterialTheme.colorScheme.primary
            )

            Text(
                text = stringResource(id = R.string.app_name),
                style = MaterialTheme.typography.titleLarge
            )

            Spacer(Modifier.height(16.dp))

            Text(
                text = stringResource(R.string.about_version_text) + BuildConfig.VERSION_NAME + "\n"
                        + stringResource(id = R.string.about_creator) + "\n"
                        + stringResource(R.string.about_licence),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.bodyLarge,
            )

            Spacer(Modifier.height(24.dp))

            Divider()

            AboutListItem(
                onClick = { uriHandler.openUri("https://github.com/willbsp/habits") },
                icon = Icons.TwoTone.DataObject,
                text = stringResource(id = R.string.about_view_source)
            )

            AboutListItem(
                onClick = { uriHandler.openUri("https://github.com/willbsp/habits/issues") },
                icon = Icons.TwoTone.BugReport,
                text = stringResource(id = R.string.about_report_an_issue)
            )

        }

    }
}

@Composable
fun AboutListItem(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    icon: ImageVector,
    text: String,
) {
    ListItem(
        modifier = modifier.clickable { onClick(); },
        headlineContent = {
            Row {
                Icon(icon, "")
                Spacer(Modifier.width(10.dp))
                Text(text)
            }
        }
    )
}

@Composable
@Preview(showBackground = true, showSystemUi = true)
fun AboutScreenPreview() {
    AboutScreen {}
}