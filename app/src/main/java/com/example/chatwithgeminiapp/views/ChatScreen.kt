package com.example.chatwithgeminiapp.views

import android.graphics.Bitmap
import android.net.Uri
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AddCircle
import androidx.compose.material.icons.rounded.Send
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color.Companion.LightGray
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.graphics.drawable.toBitmap
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImagePainter
import coil.request.ImageRequest
import coil.compose.rememberAsyncImagePainter
import coil.size.Size
import com.example.chatwithgeminiapp.R
import com.example.chatwithgeminiapp.utils.ChatUiEvent
import com.example.chatwithgeminiapp.viewmodels.ChatViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update

// This is the main screen of the app of the chat with gemini.
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen() {
    // This variable is used to get the uri state.
    val uriState = MutableStateFlow("")
    // This variable is used to get the result of the pick media launcher.
    val pickMediaLauncher =
        rememberLauncherForActivityResult(contract = ActivityResultContracts.PickVisualMedia()) { imageUri ->
            uriState.update {
                imageUri.toString()
            }
        }
    // A surface container using the 'background' color from the theme
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {

        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            text = stringResource(id = R.string.app_name),
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    ),
                )
            }
        ) {
            ChatScreenView(paddingValues = it, uriState, pickMediaLauncher)
        }
    }
}

// This is the view of the chat screen.
@Composable
fun ChatScreenView(
    paddingValues: PaddingValues,
    uriState: MutableStateFlow<String>,
    pickMediaLauncher: ManagedActivityResultLauncher<PickVisualMediaRequest, Uri?>
) {
    // This variable is used to get the chat view model.
    val chaViewModel = viewModel<ChatViewModel>()
    // This variable is used to get the chat state.
    val chatState = chaViewModel.chatState.collectAsState().value
    // This variable is used to get the focus manager.
    val focusManager = LocalFocusManager.current
    // This variable is used to get the bitmap from the uri.
    val bitmap = getBitmap(uriState)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = paddingValues.calculateTopPadding()),
        verticalArrangement = Arrangement.Bottom
    ) {
        // This is the list of the chat messages.
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(horizontal = 8.dp),
            reverseLayout = true
        ) {
            itemsIndexed(chatState.chatList) { _, chat ->
                // Validate if the message is from the user or not.
                if (chat.isFromUser) {
                    UserChatItem(
                        prompt = chat.prompt, bitmap = chat.bitmap
                    )
                } else {
                    ModelChatItem(response = chat.prompt)
                }
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp, start = 4.dp, end = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Column {
                bitmap?.let {
                    Image(
                        modifier = Modifier
                            .size(40.dp)
                            .padding(bottom = 2.dp)
                            .clip(RoundedCornerShape(6.dp)),
                        contentDescription = "picked image",
                        contentScale = ContentScale.Crop,
                        bitmap = it.asImageBitmap()
                    )
                }

                Icon(
                    modifier = Modifier
                        .size(40.dp)
                        .clickable {
                            pickMediaLauncher.launch(
                                PickVisualMediaRequest
                                    .Builder()
                                    .setMediaType(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                    .build()
                            )
                        },
                    imageVector = Icons.Rounded.AddCircle,
                    contentDescription = "Add Photo",
                    tint = MaterialTheme.colorScheme.primary
                )
            }

            // This Spacer is used to add some space between the image and the text field.
            Spacer(modifier = Modifier.width(8.dp))

            // This is the text field of the chat screen.
            TextField(
                modifier = Modifier
                    .weight(1f),
                value = chatState.prompt,
                onValueChange = {
                    chaViewModel.onEvent(ChatUiEvent.UpdatePrompt(it))
                },
                placeholder = {
                    Text(text = "Envia un mensaje")
                }
            )

            Spacer(modifier = Modifier.width(8.dp))

            // This icon is used to send the images to the gemini.
            Icon(
                modifier = Modifier
                    .size(40.dp)
                    .clickable {
                        chaViewModel.onEvent(ChatUiEvent.SendPrompt(chatState.prompt, bitmap))
                        uriState.update { "" }
                        focusManager.clearFocus()
                    },
                imageVector = Icons.Rounded.Send,
                contentDescription = "Send prompt",
                tint = MaterialTheme.colorScheme.primary
            )
        }
    }
}

// This user chat item is used to show the user chat messages.
@Composable
fun UserChatItem(prompt: String, bitmap: Bitmap?) {
    Column(
        modifier = Modifier.padding(start = 100.dp, bottom = 16.dp)
    ) {
        // Verify if the image is not null.
        bitmap?.let {
            Image(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(260.dp)
                    .padding(bottom = 2.dp)
                    .clip(RoundedCornerShape(12.dp)),
                contentDescription = "image",
                contentScale = ContentScale.Crop,
                bitmap = it.asImageBitmap()
            )
        }

        Text(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(MaterialTheme.colorScheme.primary)
                .padding(16.dp),
            text = prompt,
            fontSize = 17.sp,
            color = MaterialTheme.colorScheme.onPrimary
        )

    }
}

// This model chat item is used when the Gemini respond.
@Composable
fun ModelChatItem(response: String) {
    Column(
        modifier = Modifier.padding(end = 100.dp, bottom = 16.dp)
    ) {
        Text(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(LightGray)
                .padding(16.dp),
            text = response,
            fontSize = 17.sp,
            color = MaterialTheme.colorScheme.onPrimary
        )

    }
}

// This function is used to get the bitmap from the uri.
@Composable
private fun getBitmap(imageUris: MutableStateFlow<String>): Bitmap? {
    val uri = imageUris.collectAsState().value

    // Build the request to get the image.
    val imageState: AsyncImagePainter.State = rememberAsyncImagePainter(
        model = ImageRequest.Builder(LocalContext.current)
            .data(uri)
            .size(Size.ORIGINAL)
            .build()
    ).state

    // Verify if the image is loaded.
    if (imageState is AsyncImagePainter.State.Success) {
        return imageState.result.drawable.toBitmap()
    }

    return null
}