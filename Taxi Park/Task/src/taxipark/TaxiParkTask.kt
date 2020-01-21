package taxipark

/*
 * Task #1. Find all the drivers who performed no trips.
 */
fun TaxiPark.findFakeDrivers(): Set<Driver> =
        this.allDrivers.subtract(this.trips.map { it.driver }.toSet())

/*
 * Task #2. Find all the clients who completed at least the given number of trips.
 */
fun TaxiPark.findFaithfulPassengers(minTrips: Int): Set<Passenger> =
        if (minTrips < 1) {
            this.allPassengers
        } else {
            this.trips.flatMap { trip -> trip.passengers.map { passenger -> passenger to trip } }
                    .groupBy { it.first }
                    .filter { (passanger, trips) -> trips.size >= minTrips }
                    .keys
        }


/*
 * Task #3. Find all the passengers, who were taken by a given driver more than once.
 */
fun TaxiPark.findFrequentPassengers(driver: Driver): Set<Passenger> =
        this.trips.filter { trip -> trip.driver == driver }
                .flatMap { trip -> trip.passengers.map { passenger -> passenger to driver } }
                .groupBy { it.first }
                .filter { (passanger, drivers) -> drivers.size > 1 }
                .keys

/*
 * Task #4. Find the passengers who had a discount for majority of their trips.
 */
fun TaxiPark.findSmartPassengers(): Set<Passenger> =
        this.trips.flatMap { trip -> trip.passengers.map { passenger -> passenger to trip } }
                .groupBy { it.first }
                .mapValues { (passanger, trips) -> trips.map { it.second } }
                .mapValues { (passanger, trips) -> trips.partition { trip -> (trip.discount ?: 0.0) > 0.0 } }
                .filter { (passanger, pair) -> pair.first.size > pair.second.size }
                .keys

/*
 * Task #5. Find the most frequent trip duration among minute periods 0..9, 10..19, 20..29, and so on.
 * Return any period if many are the most frequent, return `null` if there're no trips.
 */
fun TaxiPark.findTheMostFrequentTripDurationPeriod(): IntRange? {
    val bucket = this.trips.map { trip -> trip.duration }
            .groupBy { it / 10 }
            .maxBy { (_, durations) -> durations.size }
            ?.key

    return bucket?.let {
        val start = it * 10
        val end = start + 9
        IntRange(start, end)
    }
}

/*
 * Task #6.
 * Check whether 20% of the drivers contribute 80% of the income.
 */
fun TaxiPark.checkParetoPrinciple(): Boolean {
    if (this.trips.isEmpty()) {
        return false
    } else {
        val descSortedIncomeByDriver = this.trips.groupBy { it.driver }
                .mapValues { (driver, trips) -> trips.sumByDouble { it.cost } }
                .entries
                .sortedByDescending { it.value }

        val pareTo20Threshold = this.allDrivers.size / 5 // 20 percent drivers
        val pareTo80Threshold = this.trips.sumByDouble { it.cost } * 0.8 // 80 percent income
        return descSortedIncomeByDriver.take(pareTo20Threshold).sumByDouble { it.value } >= pareTo80Threshold
    }
}