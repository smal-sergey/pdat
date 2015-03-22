package com.smalser.pdat.core;

import org.apache.commons.math3.distribution.UniformRealDistribution;

public class Test
{
    public static void main(String[] args)
    {
//        ProjectInitialEstimates pie = new ProjectInitialEstimates();
//
//        UserInitialEstimate user1 = new UserInitialEstimate(1);
//        user1.addEstimate(uniform("task1", 2, 5));
//        user1.addEstimate(triangular("task2", 2, 4, 5));
//        user1.addEstimate(trapezoidal("task3", 2, 3, 4, 5));
//        user1.addEstimate(normal("task4", 2, 3, 4, 5));
//        pie.addUserEstimates(user1);
//
//        UserInitialEstimate user2 = new UserInitialEstimate(1);
//        user2.addEstimate(uniform("task1", 4, 6));
//        user2.addEstimate(triangular("task2", 2, 3, 5));
//        user2.addEstimate(trapezoidal("task3", 1, 2, 4, 5));
//        user2.addEstimate(normal("task4", 1, 2, 3, 6));
//        pie.addUserEstimates(user2);
//
//        System.out.println(pie.getTaskEstimates().toString());

        UniformRealDistribution uniDist = new UniformRealDistribution(0, 10);

        System.out.println(uniDist.cumulativeProbability(7));
        System.out.println(uniDist.density(1));
        System.out.println(uniDist.density(3));

    }
}
