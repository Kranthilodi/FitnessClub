package org.example;

public class Review {
    private static double[][] lessonsRating;

    public Review(double[][] lessonsRating) {
        this.lessonsRating = lessonsRating;
        int i=0;
        while(i<lessonsRating.length){
            lessonsRating[i][1] = 0.0;
            lessonsRating[i][0] = 0.0;
            i++;
        }
    }
    

    public void addRating(int fitnessIndex, int customerRating){
        double totRating = lessonsRating[fitnessIndex][0] * lessonsRating[fitnessIndex][1];
        lessonsRating[fitnessIndex][0] = (totRating+customerRating) / (lessonsRating[fitnessIndex][1]+1);
        lessonsRating[fitnessIndex][1]++;
    }

    public double getAvgRating(int fitnessIndex){
        return lessonsRating[fitnessIndex][0];
    }
}
