package com.smalser.pdat.core.cpm;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import org.junit.Before;
import org.junit.Test;

import java.util.List;
import java.util.Set;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class CpmCalculatorTest
{
    Set<Task> baseTasks;
    private List<Task> baseCP;
    private CpmCalculator calc;

    @Before
    public void setUp() throws Exception
    {
        Task a = new Task("a", 3);
        Task b = new Task("b", 4, a);
        Task c = new Task("c", 2, a);
        Task e = new Task("e", 5, a);
        Task g = new Task("g", 3, a);
        Task h = new Task("h", 7, c, e, g);
        Task j = new Task("j", 8, b, h);
        Task f = new Task("f", 5, h);
        Task i = new Task("i", 6, g);
        Task m = new Task("m", 4, i);
        Task l = new Task("l", 1, j);
        Task n = new Task("n", 3, m);
        Task k = new Task("k", 2, f, n, l);
        Task z = new Task("z", 4, l);
        Task o = new Task("o", 2, k);
        Task p = new Task("p", 11, o);
        Task q = new Task("q", 7, p);
        Task r = new Task("r", 1, q);
        Task t = new Task("t", 10, o);
        Task u = new Task("u", 8, t);
        Task s = new Task("s", 2, u, r);
        Task v = new Task("v", 4, s, z);
        Task x = new Task("x", 3, v);

        baseTasks = Sets.newHashSet(a, b, c, e, f, g, h, j, i, l, m, n, k, o, p, q, r, s, t, u, v, x, z);
        baseCP = Lists.newArrayList(a, e, h, j, l, k, o, p, q, r, s, v, x);
        calc = new CpmCalculator();
    }

    @Test
    public void test_long_parallel_task() throws Exception
    {
        Task xx = new Task("xx", 10);
        Task xxx = new Task("xxx", 100, xx);

        List<Task> tasks = calc.criticalPath(Sets.union(baseTasks, Sets.newHashSet(xx, xxx)));

        assertThat(tasks, is(Lists.newArrayList(xx, xxx)));
    }

    @Test
    public void test_short_parallel_task() throws Exception
    {
        Task xx = new Task("xx", 10);
        Task xxx = new Task("xxx", 10, xx);

        List<Task> tasks = calc.criticalPath(Sets.union(baseTasks, Sets.newHashSet(xx, xxx)));

        assertThat(tasks, is(baseCP));
    }

    @Test
    public void test_base_cp() throws Exception
    {
        List<Task> tasks = calc.criticalPath(baseTasks);
        assertThat(tasks, is(baseCP));
    }
}