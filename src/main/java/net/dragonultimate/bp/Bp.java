package net.dragonultimate.bp;

import net.dragonultimate.stats.Dex;
import net.dragonultimate.stats.Spi;
import net.dragonultimate.stats.Str;

public class Bp {
    private final Str s;
    private final Dex d;
    private final Spi sp;

    public Bp() {
        this(new Str(), new Dex(), new Spi());
    }

    public Bp(Str str, Dex dex, Spi spiritualist) {
        this.s = str;
        this.d = dex;
        this.sp = spiritualist;
    }

    public int calculateWarriorBp( ) {
        return s.getStr() * 10
                + d.getDex() * 10
                + sp.getSpi() * 4;
    }

    public int calculateSpiritualistBp( ) {
        return s.getStr() * 4
                + d.getDex() * 7
                + sp.getSpi() * 13;
    }
    public int calculateMartialArtesBp( ) {
        return s.getStr() * 8
                + d.getDex() * 8
                + sp.getSpi() * 8;
    }
}
