package com.smalser.pdat.core.calculator;

import com.smalser.pdat.core.structure.TaskInitialEstimate;
import org.apache.commons.math3.analysis.integration.TrapezoidIntegrator;
import org.apache.commons.math3.analysis.integration.UnivariateIntegrator;
import org.apache.commons.math3.analysis.solvers.AbstractUnivariateSolver;
import org.apache.commons.math3.analysis.solvers.BisectionSolver;
import org.apache.commons.math3.distribution.AbstractRealDistribution;

import java.util.Set;

public class BetaFinder
{
    private final Set<TaskInitialEstimate> estimates;
    private final AbstractRealDistribution distribution;
    private final double alpha;
    private final double gamma;
    private final double precision;
    private UnivariateIntegrator integrator;

    public BetaFinder(Set<TaskInitialEstimate> estimates, AbstractRealDistribution distribution, double alpha,
                      double gamma)
    {
        this.estimates = estimates;
        this.distribution = distribution;
        this.alpha = alpha;
        this.gamma = gamma;
        this.precision = gamma / 100;
//        integrator = new SimpsonIntegrator(1, 30);
        integrator = new TrapezoidIntegrator(0.001, 0.001, 1, 50);
    }

    public double findMinBeta()
    {
        double maxBeta = estimates.stream().map(e -> e.b2).max(Double::compareTo).get();
        AbstractUnivariateSolver bisectionSolver = new BisectionSolver(precision);
//        AbstractUnivariateSolver bisectionSolver = new BrentSolver(precision);
        return bisectionSolver.solve(100, this::integrateDensity, alpha + 0.01, maxBeta, maxBeta);
    }

    private double integrateDensity(double beta)
    {
        return integrator.integrate(100, distribution::density, alpha, beta) - gamma;
    }
}










