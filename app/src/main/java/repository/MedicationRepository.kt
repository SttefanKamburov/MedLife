package repository

import models.MedicationDto

public class MedicationRepository () {
    companion object{
        fun getMedicationArray() = arrayOf(
            MedicationDto("Aspirine", null, "3", "3", 40),
            MedicationDto("Anavar", "anavar icon", "2", "1", 90),
            MedicationDto("Aspartam", null, "1", "2", 50),
            MedicationDto("Sugar", null, "4", "4", 30),
            MedicationDto("Anti age", null, "5", "5", 20),
        )
    }
}