package com.smalser.pdat.core.calculator;

import org.apache.commons.math3.analysis.integration.TrapezoidIntegrator;
import org.apache.commons.math3.analysis.integration.UnivariateIntegrator;
import org.apache.commons.math3.analysis.solvers.AbstractUnivariateSolver;
import org.apache.commons.math3.analysis.solvers.BisectionSolver;
import org.apache.commons.math3.distribution.AbstractRealDistribution;

public class BetaFinder
{
    private final AbstractRealDistribution distribution;
    private final double precision;
    private UnivariateIntegrator integrator;
    private final TaskConstraints taskConstraints;

    public BetaFinder(AbstractRealDistribution distribution, TaskConstraints taskConstraints)
    {
        this.taskConstraints = taskConstraints;
        this.distribution = distribution;
        this.precision = taskConstraints.gamma / 100;
//        integrator = new SimpsonIntegrator(1, 30);
        integrator = new TrapezoidIntegrator(0.001, 0.001, 1, 50);
    }

    public double findMinBeta()
    {
        double maxBeta = taskConstraints.rightBound;
        AbstractUnivariateSolver bisectionSolver = new BisectionSolver(precision);
//        AbstractUnivariateSolver bisectionSolver = new BrentSolver(precision);
        return bisectionSolver.solve(100, this::integrateDensity, taskConstraints.alpha + 0.01, maxBeta, maxBeta);
    }

    private double integrateDensity(double beta)
    {
        return integrator.integrate(100, distribution::density, taskConstraints.alpha, beta) - taskConstraints.gamma;
    }
}










