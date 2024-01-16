package ch.chrigu.wotr.fellowship

data class Fellowship(val progress: Int = 0, val corruption: Int = 0) {
    init {
        require(progress in 0..12) { "Progress should be between 0 and 12, but was $progress" }
        require(corruption in 0..12) { "Corruption should be between 0 and 12, but was $corruption" }
    }
}
