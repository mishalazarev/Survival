package white.ball.survival.domain.model.music_controller

data class MusicController(
    var isPlayMainMusic: Boolean = false,
    var isPlayFightWithBossMusic: Boolean = false,
    var isMusicServiceBound: Boolean = false
)