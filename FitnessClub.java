package org.example;

import java.util.*;

public class FitnessClub {
    private static final int NUM_DAYS = 16;
    private static final int LESSONS_PER_DAY = 2;
    private static final int MAX_CAPACITY = 5;

    private static final String[] FITNESS_TYPES = {"SPIN", "YOGA", "BODYSCULPT", "ZUMBA"};
    private static final double[] PRICES = {50.0, 62.0, 100.0, 25.0};

    private static int[][] bookings = new int[NUM_DAYS][LESSONS_PER_DAY * NUM_DAYS];
    private static double[] incomes = new double[FITNESS_TYPES.length];
    private static Review review = new Review(new double[FITNESS_TYPES.length][2]);

    private static final String[] DAYS_OF_WEEK = {"Saturday", "Sunday"};
    private static final String[] TIMES_OF_DAY = {"9:00am", "10:30am", "12:00pm", "1:30pm"};


    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        boolean isContinue = true;

        while (isContinue) {
            System.out.println("---------------------------------------------------");
            System.out.println("Welcome to the Weekend Fitness Club booking system!");
            System.out.println("---------------------------------------------------");
            System.out.println("1. Book a group fitness lesson");
            System.out.println("2. Change/Cancel a booking");
            System.out.println("3. Attend a lesson");
            System.out.println("4. Monthly lesson report");
            System.out.println("5. Monthly champion fitness type report");
            System.out.println("0. Exit");
            System.out.print("Please select an option:");
            int option = getUserOption(scanner);

            switch (option) {
                case 1:
                    bookLesson(scanner);
                    break;
                case 2:
                    changeOrCancelBooking(scanner);
                    break;
                case 3:
                    attendLesson(scanner);
                    break;
                case 4:
                    generateMonthlyLessonReport();
                    break;
                case 5:
                    generateMonthlyFitnessTypeReport();
                    break;
                case 0:
                    isContinue = false;
                    System.out.println("Thank you for using the Weekend Fitness Club booking system!");
                    break;
                default:
                    System.out.println("Invalid option. Please try again.");
            }
        }
    }
    private static int getUserOption(Scanner scanner) {
        int option = scanner.nextInt();
        scanner.nextLine();
        return option;
    }

    private static void bookLesson(Scanner scanner) {
        System.out.print("Please enter your customer ID: ");
        int customerId = scanner.nextInt();
        System.out.println("----------------------------------------");
        System.out.println("Please select how you want to search for available lessons:");
        System.out.println("----------------------------------------");
        System.out.println("1. By day (Saturday or Sunday)");
        System.out.println("2. By fitness type");

        int option = scanner.nextInt();

        if (option < 1 || option > 2) {
            System.out.println("Invalid option. Please try again.");
            return;
        }

        int[] availableLessons;
        String fitnessTypeChoice = "";
        if (option == 1) {
            int day = 1;
            availableLessons = getAvailableLessonsForDay(day);
        } else {
            System.out.println("Please enter the fitness type:");
            System.out.println("(Spin,Yoga,BodySculpt,Zumba)");

            fitnessTypeChoice += scanner.next().toUpperCase();
            int day=1;
            availableLessons = getAvailableLessonsForDay(day);
        }

        if (availableLessons.length == 0) {
            System.out.println("Sorry, there are no available lessons for your selected option.");
            return;
        }

        System.out.println("Available lessons: ");
        System.out.println("-------------------");
        if(option == 1) {
            for (int i = 0; i < availableLessons.length; i++) {
                int lessonIndex = availableLessons[i];
                int day = i % 2;
                int lesson = lessonIndex % FITNESS_TYPES.length;
                String fitnessType = FITNESS_TYPES[lesson];

                System.out.println((i + 1) + ". " + fitnessType + " on " + getDayOfWeek(day % 2) +
                        " at " + getTimeOfDay(lesson) + " with a price of $" + PRICES[lesson]);
            }
        }

        int typeIndex = getFitnessTypeIndex(fitnessTypeChoice);
        if(option==2) {
            for (int i = typeIndex; i < availableLessons.length; i+=FITNESS_TYPES.length) {
                int lessonIndex = availableLessons[i];
                int day = i % 2;
                int lesson = lessonIndex % FITNESS_TYPES.length;
                String fitnessType = FITNESS_TYPES[lesson];

                System.out.println((i + 1) + ". " + fitnessType + " on " + getDayOfWeek(day % 2) +
                        " at " + getTimeOfDay(lesson) + " with a price of $" + PRICES[lesson]);
            }
        }


        System.out.print("Please select a lesson: ");
        int lessonChoice = scanner.nextInt();
        int lessonIndex = availableLessons[lessonChoice - 1];
        int day = lessonIndex % LESSONS_PER_DAY;
        int lesson = lessonIndex % FITNESS_TYPES.length;
        String fitnessType = FITNESS_TYPES[lesson];

        System.out.println("You have selected " + fitnessType + " on " + getDayOfWeek(day) +
                " at " + getTimeOfDay(lesson) + " with a price of $" + PRICES[lesson]);

        if (bookings[day][lessonIndex] >= MAX_CAPACITY) {
            System.out.println("Sorry, the lesson is fully booked.");
            return;
        }

        if(option == 1) {
            bookings[day][lessonIndex]++;
        }
        if(option == 2){
            bookings[day][getFitnessTypeIndex(fitnessTypeChoice)]++;
        }

        System.out.println("Booking successful! Your booking ID is " + generateBookingId(day, lessonIndex, customerId));
    }


    private static void changeOrCancelBooking(Scanner scanner) {
        System.out.println("Please enter your booking ID:");
        int bookingId = scanner.nextInt();

        int[] bookingDetails = parseBookingId(bookingId);
        if (bookingDetails == null) {
            System.out.println("Invalid booking ID. Please try again.");
            return;
        }

        int day = bookingDetails[0];
        int lessonIndex = bookingDetails[1];
        int lesson = lessonIndex % FITNESS_TYPES.length;
        int customerId = bookingDetails[2];

        if(bookings[day][lessonIndex] == 0){
            System.out.println("Booking Id not valid!");
            return;
        }

        String fitnessType = FITNESS_TYPES[lesson];

        System.out.println("You have booked " + fitnessType + " on " + getDayOfWeek(day%2) +
                " at " + getTimeOfDay(lesson) + " with a price of $" + PRICES[lesson]);

        System.out.println("Please select an option:");
        System.out.println("1. Change booking to another lesson");
        System.out.println("2. Cancel booking");

        int option = scanner.nextInt();

        if (option == 1) {
            int[] availableLessons = getAvailableLessonsForDay(1);

            if (availableLessons.length == 0) {
                System.out.println("Sorry, there are no available lessons for your selected option.");
                return;
            }

            System.out.println("Available lessons: ");
            System.out.println("___________________");
            for (int i = 0; i < availableLessons.length; i++) {
                int lessonIndexA = availableLessons[i];
                int dayA = i % 2;
                int lessonA = lessonIndexA % FITNESS_TYPES.length;
                String fitnessTypeA = FITNESS_TYPES[lessonA];

                System.out.println((i + 1) + ". " + fitnessTypeA + " on " + getDayOfWeek(dayA % 2) +
                        " at " + getTimeOfDay(lessonA) + " with a price of $" + PRICES[lessonA]);
            }

            System.out.println("Please select a new lesson (1-" + availableLessons.length + "):");
            int newLessonChoice = scanner.nextInt();
            int newLessonIndex = availableLessons[newLessonChoice - 1];
            int newDay = newLessonIndex % LESSONS_PER_DAY;
            int newLesson = newLessonIndex % FITNESS_TYPES.length;
            System.out.println("new lesson" + newLessonIndex);

            System.out.println("You have selected " + FITNESS_TYPES[newLesson] + " on " + getDayOfWeek(newDay%2) +
                    " at " + getTimeOfDay(newLesson) + " with a price of $" + PRICES[newLesson]);

            bookings[day][lessonIndex]--;
            bookings[newDay][newLessonIndex]++;
            System.out.println("Booking changed successfully! Your new booking ID is " + generateBookingId(newDay, newLessonIndex, customerId));
        } else if (option == 2) {
            bookings[day][lessonIndex]--;
            int fitnessTypeIndex = getFitnessTypeIndex(fitnessType);
            incomes[fitnessTypeIndex] -= PRICES[lesson];
            System.out.println("Booking cancelled successfully!");
        } else {
            System.out.println("Invalid option. Please try again.");
        }
    }


    private static void attendLesson(Scanner scanner) {
        System.out.println("Please enter your booking ID:");
        int bookingId;
        try {
            bookingId = scanner.nextInt();
        } catch (InputMismatchException e) {
            System.out.println("Invalid input. Please enter a valid booking ID.");
            scanner.next();
            return;
        }

        int[] bookingDetails = parseBookingId(bookingId);
        if (bookingDetails == null) {
            System.out.println("Invalid booking ID. Please try again.");
            return;
        }

        int day = bookingDetails[0];
        int lessonIndex = bookingDetails[1];
        int lesson = lessonIndex % FITNESS_TYPES.length;

        if (bookings[day][lessonIndex] == 0) {
            System.out.println("Sorry, this lesson is not booked by anyone.");
            return;
        }


        String fitnessType = FITNESS_TYPES[lesson];
        System.out.println("Welcome to the " + fitnessType + " class on " + getDayOfWeek(day%2) +
                " at " + getTimeOfDay(lesson));

        System.out.println("Please rate the lesson (1-5):");
        int rating = scanner.nextInt();
        scanner.nextLine();
        System.out.println("Please provide your feedback:");
        String feedback = scanner.nextLine();

        System.out.println("Thank you for your rating of " + rating + " and feedback: " + feedback);

        double income = PRICES[lesson];
        incomes[lesson] += income;
        System.out.println("Your account will be charged $" + income + " for attending the lesson.");
        System.out.println();

        review.addRating(lesson, rating);
    }


    public static void generateMonthlyLessonReport() {
        int totalBookings = 0;
        double totalIncome = 0;

        System.out.println();
        System.out.println("----------------------");
        System.out.println("Monthly lesson report:");
        System.out.println("----------------------");

        for (int i = 0; i < NUM_DAYS; i++) {
            for (int j = 0; j < LESSONS_PER_DAY * NUM_DAYS; j++) {
                if (bookings[i][j] > 0 && review.getAvgRating(j% FITNESS_TYPES.length) > 0) {
                    int fitnessTypeIndex = j % FITNESS_TYPES.length;
                    String fitnessType = FITNESS_TYPES[fitnessTypeIndex];
                    double income = PRICES[fitnessTypeIndex] * bookings[i][j];
                    totalBookings += bookings[i][j];
                    totalIncome += income;
                    System.out.println(fitnessType + " : No of Customers " + bookings[i][j] +
                            " with Avg Rating "+ review.getAvgRating(fitnessTypeIndex));
                }
            }
        }

        System.out.println();
        System.out.println("Total bookings: " + totalBookings);
        System.out.println("Total income: $" + totalIncome);
    }

    public static void generateMonthlyFitnessTypeReport() {
        System.out.println();
        System.out.println("-------------------------------------");
        System.out.println("Monthly champion fitness type report:");
        System.out.println("-------------------------------------");
        System.out.println();
        int maxIndex = 0;
        double maxIncome = incomes[0];

        for (int i = 0; i < FITNESS_TYPES.length; i++) {
            if (incomes[i] > maxIncome) {
                maxIndex = i;
                maxIncome = incomes[i];
            }
            System.out.println(FITNESS_TYPES[i] + " : Total income - " + incomes[i] );
        }

        System.out.println();
        if(maxIncome>0) {
            System.out.println("The champion fitness type for this month is " + FITNESS_TYPES[maxIndex] +
                    " with a total income of $" + maxIncome);
        }else {
            System.out.println("No income as of now!");
        }
        System.out.println();
    }

    private static int[] getAvailableLessonsForDay(int day) {
        int[] availableLessons = new int[LESSONS_PER_DAY * NUM_DAYS];
        int index = 0;
        for (int i = 0; i < LESSONS_PER_DAY * NUM_DAYS; i++) {
            if (bookings[day][i] < MAX_CAPACITY) {
                availableLessons[index] = i;
                index++;
            }
        }

        return Arrays.copyOfRange(availableLessons, 0, index);
    }


    private static int getFitnessTypeIndex(String fitnessType) {
        for (int i = 0; i < FITNESS_TYPES.length; i++) {
            if (FITNESS_TYPES[i].equals(fitnessType)) {
                return i;
            }
        }
        return -1;
    }

    private static int generateBookingId(int day, int lesson, int customerId) {
        return day * 10000 + lesson * 100 + customerId;
    }


    private static int[] parseBookingId(int bookingId) {
        int day = bookingId / 10000;
        int lesson = (bookingId % 10000) / 100;
        int customerId = bookingId % 10;

        if (day < 0 || day >= NUM_DAYS || lesson < 0 || lesson >= (NUM_DAYS * LESSONS_PER_DAY) ||
                customerId <= 0 || customerId > MAX_CAPACITY) {
            System.out.println("Invalid booking ID.");
            return null;
        }

        return new int[] {day, lesson, customerId};
    }

    private static String getDayOfWeek(int day) {
        if(day <0 || day>=DAYS_OF_WEEK.length){
            return "Invalid day";
        }
        return DAYS_OF_WEEK[day];
    }

    private static String getTimeOfDay(int lesson) {
        if(lesson <0 || lesson >= TIMES_OF_DAY.length){
            return "Invalid lesson";
        }
        return TIMES_OF_DAY[lesson];
    }

}
