package org.opennms.netmgt.provision.service.puppet;

import org.opennms.netmgt.provision.service.puppet.tools.Map2Bean;
import org.opennms.netmgt.provision.service.puppet.tools.RequistionAssetGen;

class PuppetModel {
    @Map2Bean(mapKeyName = "processor0")
    @RequistionAssetGen
    public String cpu = "";

    @Map2Bean
    @RequistionAssetGen
    public String manufacturer = "";

    @Map2Bean(mapKeyName = "lsbdistdescription")
    @RequistionAssetGen
    public String operatingsystem = "";

    @Map2Bean
    @RequistionAssetGen
    public String serialnumber = "";

    @Map2Bean(mapKeyName = "productname")
    @RequistionAssetGen
    public String modelnumber = "";

    public String getCpu() {
        return cpu;
    }

    public void setCpu(String cpu) {
        this.cpu = cpu;
    }

    public String getManufacturer() {
        return manufacturer;
    }

    public void setManufacturer(String manufacturer) {
        this.manufacturer = manufacturer;
    }

    public String getModelnumber() {
        return modelnumber;
    }

    public void setModelnumber(String modelnumber) {
        this.modelnumber = modelnumber;
    }

    public String getOperatingsystem() {
        return operatingsystem;
    }

    public void setOperatingsystem(String operatingsystem) {
        this.operatingsystem = operatingsystem;
    }

    public String getSerialnumber() {
        return serialnumber;
    }

    public void setSerialnumber(String serialnumber) {
        this.serialnumber = serialnumber;
    }
}
