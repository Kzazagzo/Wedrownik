package pl.put.szlaki.util

object TimeUtils {
    fun roundNumber(sum: Double): Double = String.format("%.2f", sum).toDouble()

    fun formatTime(timeInSeconds: Long): String {
        val hours = timeInSeconds / 3600
        val minutes = (timeInSeconds % 3600) / 60
        val seconds = timeInSeconds % 60

        val formattedHours = if (hours > 0) "%02d:".format(hours) else "00:"
        val formattedMinutes = "%02d:".format(minutes)
        val formattedSeconds = "%02d".format(seconds)

        return if (hours > 0) {
            "$formattedHours$formattedMinutes$formattedSeconds"
        } else {
            "$formattedMinutes$formattedSeconds"
        }
    }
}
