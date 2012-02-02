package org.opennms.netmgt.provision.service.puppet.tools;

class SampleBean {
    @Map2Bean
    @RequistionAssetGen
    private String o;
    
    @Map2Bean
    @RequistionAssetGen
    private String p;
    
    @Map2Bean
    @RequistionAssetGen
    private String e;
    
    @Map2Bean
    @RequistionAssetGen
    private String n;
    
    @Map2Bean(mapKeyName = "NMS")
    @RequistionAssetGen(assetName = "OpenNMS")
    private String nms;

    public String getE() {
        return e;
    }

    public void setE(String e) {
        this.e = e;
    }

    public String getN() {
        return n;
    }

    public void setN(String n) {
        this.n = n;
    }

    public String getNms() {
        return nms;
    }

    public void setNms(String nms) {
        this.nms = nms;
    }

    public String getO() {
        return o;
    }

    public void setO(String o) {
        this.o = o;
    }

    public String getP() {
        return p;
    }

    public void setP(String p) {
        this.p = p;
    }

    @Override
    public String toString() {
        return "SampleBean{" + "o=" + o + ", p=" + p + ", e=" + e + ", n=" + n + ", nms=" + nms + '}';
    }
}