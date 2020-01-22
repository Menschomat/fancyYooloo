package helper;

import java.util.List;

public class StatList {

    private List<Stat> statList;

    private int anzahlSpiele;

    public int getAnzahlSpiele() {
        return anzahlSpiele;
    }

    public void setAnzahlSpiele(int anzahlSpiele) {
        this.anzahlSpiele = anzahlSpiele;
    }

    public List<Stat> getStatList() {
        return statList;
    }

    public void setStatList(List<Stat> statList) {
        this.statList = statList;
    }

    public void updateStatList(Boolean stichGewonnen, int stichnummer, int siegerKarte) {
        for (Stat stat : this.statList) {
            if (Integer.valueOf(stat.stichNummer).equals(stichnummer + 1)) {
                for (Stat.WinRates winRates : stat.winRates) {
                    double asdf = 100.00 / anzahlSpiele;
                    if (stichGewonnen) {
                        if (siegerKarte == winRates.kartenWert) {
                            winRates.winrate += asdf;
                        } else {
                            winRates.winrate -= asdf;
                        }
                    } else {
                        if (siegerKarte == winRates.kartenWert) {
                            winRates.winrate -= asdf;
                        } else {
                            winRates.winrate += asdf;
                        }
                    }
                }
            }
        }
    }

    public class Stat {

        private String stichNummer;

        private List<WinRates> winRates;

        public String getStichNummer() {
            return stichNummer;
        }

        public void setStichNummer(String stichNummer) {
            this.stichNummer = stichNummer;
        }

        public List<WinRates> getWinRates() {
            return winRates;
        }

        public void setWinRates(List<WinRates> winRates) {
            this.winRates = winRates;
        }

        public class WinRates {

            private int kartenWert;

            private Double winrate;

            public int getKartenWert() {
                return kartenWert;
            }

            public void setKartenWert(int kartenWert) {
                this.kartenWert = kartenWert;
            }

            public Double getWinrate() {
                return winrate;
            }

            public void setWinrate(Double winrate) {
                this.winrate = winrate;
            }
        }
    }
}
