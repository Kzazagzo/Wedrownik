package pl.put.szlaki.ui.components.common

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun ErrorFragment(error: String? = null) {
    val emoticons =
        listOf(
            "(·_·)",
            "(◕‿◕)",
            "(¬‿¬)",
            "(˘︹˘)",
            "(ಠ_ಠ)",
            "(✿◠‿◠)",
            "(ಠ‿ಠ)",
            "(¬‿¬)",
            "(⌐■_■)",
            "(•_•)",
            "(≧◡≦)",
            "(￣^￣)",
            "(＠_＠;)",
            "(︶︹︺)",
            "(T_T)",
            "(>_<)",
            "(⊙_◎)",
            "(ಠ‿↼)",
            "(°ロ°)☝",
            "(ʘ‿ʘ)",
            "(✧ω✧)",
            "(ಥ﹏ಥ)",
            "(ಥ‿ಥ)",
            "(ಥ_ಥ)",
            "(ʘ‿ʘ)╯",
            "(╥_╥)",
            "(ಠ‿↼)",
            "(´･_･`)",
            "(╯︵╰,)",
            "(´･_･)っ",
            "(⌒_⌒;)",
            "(¬‿¬)",
            "(¬_¬)",
            "(⊙_⊙)",
            "(≧ω≦)",
            "(≧∇≦)/",
            "(￣▽￣)ノ",
            "(≧▽≦)",
            "(^_^)",
            "(✧_✧)",
            "(¬‿¬ )",
            "(◕‿◕✿)",
            "(ᵔᴥᵔ)",
            "(♥ω♥*)",
            "(｡♥‿♥｡)",
            "(￣︿￣)",
            "( ‾ʖ̫‾)",
            "(¬_¬)",
            "( ◕‿◕)",
            "(ﾟヮﾟ)",
            "(✿´‿`)",
            "(･_･)",
            "(≧◡≦)",
            "( ≧Д≦)",
            "(╯︵╰,)",
            "(☉‿☉✿)",
            "(≖_≖ )",
            "(≖‿≖)",
            "(⊙ω⊙)",
            "(⊙︿⊙)",
            "(⊙_☉)",
            "(☉‿☉✿)",
            "(☉_☉)",
            "(⌐■_■)",
            "(◉‿◉)",
            "(◉_◉)",
            "(╥_╥)",
        )

    val randomEmoticon = remember { emoticons.random() }

    Surface(
        color = MaterialTheme.colorScheme.background,
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = randomEmoticon,
                style = MaterialTheme.typography.bodyLarge.copy(fontSize = 88.sp, fontWeight = FontWeight.Bold),
                modifier = Modifier.padding(bottom = 16.dp),
            )
            if (error?.isNotEmpty() == true) {
                Text(
                    text = "$error",
                    textAlign = TextAlign.Center,
                )
            }
        }
    }
}
