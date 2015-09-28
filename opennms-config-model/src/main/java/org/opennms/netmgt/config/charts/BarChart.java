/*******************************************************************************
 * This file is part of OpenNMS(R).
 *
 * Copyright (C) 2002-2015 The OpenNMS Group, Inc.
 * OpenNMS(R) is Copyright (C) 1999-2015 The OpenNMS Group, Inc.
 *
 * OpenNMS(R) is a registered trademark of The OpenNMS Group, Inc.
 *
 * OpenNMS(R) is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published
 * by the Free Software Foundation, either version 3 of the License,
 * or (at your option) any later version.
 *
 * OpenNMS(R) is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with OpenNMS(R).  If not, see:
 *      http://www.gnu.org/licenses/
 *
 * For more information contact:
 *     OpenNMS(R) Licensing <license@opennms.org>
 *     http://www.opennms.org/
 *     http://www.opennms.com/
 *******************************************************************************/

package org.opennms.netmgt.config.charts;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Class BarChart.
 * 
 * @version $Revision$ $Date$
 */
@XmlRootElement(name = "bar-chart")
@XmlAccessorType(XmlAccessType.FIELD)
public class BarChart implements Serializable {
    private static final long serialVersionUID = 7815041249463797695L;

    private static final boolean DEFAULT_DRAW_BAR_OUTLINE = true;
    private static final boolean DEFAULT_SHOW_LEGEND = true;
    private static final boolean DEFAULT_SHOW_TOOLTIPS = false;
    private static final boolean DEFAULT_SHOW_URLS = false;

    /**
     * Field _name.
     */
    @XmlAttribute(name="name")
    private String _name;

    /**
     * Field _domainAxisLabel.
     */
    @XmlAttribute(name="domain-axis-label")
    private String _domainAxisLabel;

    /**
     * Field _rangeAxisLabel.
     */
    @XmlAttribute(name="range-axis-label")
    private String _rangeAxisLabel;

    /**
     * Field _subLabelClass.
     */
    @XmlAttribute(name="sub-label-class")
    private String _subLabelClass;

    /**
     * Field _seriesColorClass.
     */
    @XmlAttribute(name="series-color-class")
    private String _seriesColorClass;

    /**
     * Field _drawBarOutline.
     */
    @XmlAttribute(name="draw-bar-outline")
    private Boolean _drawBarOutline;

    /**
     * Field _showLegend.
     */
    @XmlAttribute(name="show-legend")
    private Boolean _showLegend;

    /**
     * Field _showToolTips.
     */
    @XmlAttribute(name="show-tool-tips")
    private Boolean _showToolTips;

    /**
     * Field _showUrls.
     */
    @XmlAttribute(name="show-urls")
    private Boolean _showUrls = false;

    /**
     * Field _variation.
     */
    @XmlAttribute(name="variation")
    private String _variation;

    /**
     * Field _plotOrientation.
     */
    @XmlAttribute(name="plot-orientation")
    private String _plotOrientation;

    /**
     * Field _title.
     */
    @XmlElement(name="title")
    private Title _title;

    /**
     * Field _imageSize.
     */
    @XmlElement(name="image-size")
    private ImageSize _imageSize;

    /**
     * Field _subTitleList.
     */
    @XmlElement(name="sub-title")
    private List<SubTitle> _subTitleList = new ArrayList<>();

    /**
     * Field _gridLines.
     */
    @XmlElement(name="grid-lines")
    private GridLines _gridLines;

    /**
     * Field _seriesDefList.
     */
    @XmlElement(name="series-def")
    private List<SeriesDef> _seriesDefList = new ArrayList<>();

    /**
     * Field _plotBackgroundColor.
     */
    @XmlElement(name="plot-background-color")
    private PlotBackgroundColor _plotBackgroundColor;

    /**
     * Field _chartBackgroundColor.
     */
    @XmlElement(name="chart-background-color")
    private ChartBackgroundColor _chartBackgroundColor;

      //-----------/
     //- Methods -/
    //-----------/

    /**
     * 
     * 
     * @param vSeriesDef
     * @throws IndexOutOfBoundsException if the index
     * given is outside the bounds of the collection
     */
    public void addSeriesDef(
            final SeriesDef vSeriesDef)
    throws IndexOutOfBoundsException {
        _seriesDefList.add(vSeriesDef);
    }

    /**
     * 
     * 
     * @param index
     * @param vSeriesDef
     * @throws IndexOutOfBoundsException if the index
     * given is outside the bounds of the collection
     */
    public void addSeriesDef(
            final int index,
            final SeriesDef vSeriesDef)
    throws IndexOutOfBoundsException {
        _seriesDefList.add(index, vSeriesDef);
    }

    /**
     * 
     * 
     * @param vSubTitle
     * @throws IndexOutOfBoundsException if the index
     * given is outside the bounds of the collection
     */
    public void addSubTitle(
            final SubTitle vSubTitle)
    throws IndexOutOfBoundsException {
        _subTitleList.add(vSubTitle);
    }

    /**
     * 
     * 
     * @param index
     * @param vSubTitle
     * @throws IndexOutOfBoundsException if the index
     * given is outside the bounds of the collection
     */
    public void addSubTitle(
            final int index,
            final SubTitle vSubTitle)
    throws IndexOutOfBoundsException {
        _subTitleList.add(index, vSubTitle);
    }

    /**
     */
    public void deleteDrawBarOutline(
    ) {
        _drawBarOutline= null;
    }

    /**
     */
    public void deleteShowLegend(
    ) {
        _showLegend= null;
    }

    /**
     */
    public void deleteShowToolTips(
    ) {
        _showToolTips= null;
    }

    /**
     */
    public void deleteShowUrls(
    ) {
        _showUrls= null;
    }

    /**
     * Method enumerateSeriesDef.
     * 
     * @return an Enumeration over all possible elements of this
     * collection
     */
    public Enumeration<SeriesDef> enumerateSeriesDef(
    ) {
        return Collections.enumeration(_seriesDefList);
    }

    /**
     * Method enumerateSubTitle.
     * 
     * @return an Enumeration over all possible elements of this
     * collection
     */
    public Enumeration<SubTitle> enumerateSubTitle(
    ) {
        return Collections.enumeration(_subTitleList);
    }

    /**
     * Overrides the Object.equals method.
     * 
     * @param obj
     * @return true if the objects are equal.
     */
    @Override()
    public boolean equals(
            final Object obj) {
        if ( this == obj )
            return true;
        
        if (obj instanceof BarChart) {
        
            BarChart temp = (BarChart)obj;
            if (_name != null) {
                if (temp._name == null) return false;
                else if (!(_name.equals(temp._name))) 
                    return false;
            }
            else if (temp._name != null)
                return false;
            if (_domainAxisLabel != null) {
                if (temp._domainAxisLabel == null) return false;
                else if (!(_domainAxisLabel.equals(temp._domainAxisLabel))) 
                    return false;
            }
            else if (temp._domainAxisLabel != null)
                return false;
            if (_rangeAxisLabel != null) {
                if (temp._rangeAxisLabel == null) return false;
                else if (!(_rangeAxisLabel.equals(temp._rangeAxisLabel))) 
                    return false;
            }
            else if (temp._rangeAxisLabel != null)
                return false;
            if (_subLabelClass != null) {
                if (temp._subLabelClass == null) return false;
                else if (!(_subLabelClass.equals(temp._subLabelClass))) 
                    return false;
            }
            else if (temp._subLabelClass != null)
                return false;
            if (_seriesColorClass != null) {
                if (temp._seriesColorClass == null) return false;
                else if (!(_seriesColorClass.equals(temp._seriesColorClass))) 
                    return false;
            }
            else if (temp._seriesColorClass != null)
                return false;
            if (!Objects.equals(_drawBarOutline, temp._drawBarOutline))
                return false;
            if (!Objects.equals(_showLegend, temp._showLegend))
                return false;
            if (!Objects.equals(_showToolTips, temp._showToolTips))
                return false;
            if (!Objects.equals(_showUrls,  temp._showUrls))
                return false;
            if (_variation != null) {
                if (temp._variation == null) return false;
                else if (!(_variation.equals(temp._variation))) 
                    return false;
            }
            else if (temp._variation != null)
                return false;
            if (_plotOrientation != null) {
                if (temp._plotOrientation == null) return false;
                else if (!(_plotOrientation.equals(temp._plotOrientation))) 
                    return false;
            }
            else if (temp._plotOrientation != null)
                return false;
            if (_title != null) {
                if (temp._title == null) return false;
                else if (!(_title.equals(temp._title))) 
                    return false;
            }
            else if (temp._title != null)
                return false;
            if (_imageSize != null) {
                if (temp._imageSize == null) return false;
                else if (!(_imageSize.equals(temp._imageSize))) 
                    return false;
            }
            else if (temp._imageSize != null)
                return false;
            if (_subTitleList != null) {
                if (temp._subTitleList == null) return false;
                else if (!(_subTitleList.equals(temp._subTitleList))) 
                    return false;
            }
            else if (temp._subTitleList != null)
                return false;
            if (_gridLines != null) {
                if (temp._gridLines == null) return false;
                else if (!(_gridLines.equals(temp._gridLines))) 
                    return false;
            }
            else if (temp._gridLines != null)
                return false;
            if (_seriesDefList != null) {
                if (temp._seriesDefList == null) return false;
                else if (!(_seriesDefList.equals(temp._seriesDefList))) 
                    return false;
            }
            else if (temp._seriesDefList != null)
                return false;
            if (_plotBackgroundColor != null) {
                if (temp._plotBackgroundColor == null) return false;
                else if (!(_plotBackgroundColor.equals(temp._plotBackgroundColor))) 
                    return false;
            }
            else if (temp._plotBackgroundColor != null)
                return false;
            if (_chartBackgroundColor != null) {
                if (temp._chartBackgroundColor == null) return false;
                else if (!(_chartBackgroundColor.equals(temp._chartBackgroundColor))) 
                    return false;
            }
            else if (temp._chartBackgroundColor != null)
                return false;
            return true;
        }
        return false;
    }

    /**
     * Returns the value of field 'chartBackgroundColor'.
     * 
     * @return the value of field 'ChartBackgroundColor'.
     */
    public ChartBackgroundColor getChartBackgroundColor(
    ) {
        return _chartBackgroundColor;
    }

    /**
     * Returns the value of field 'domainAxisLabel'.
     * 
     * @return the value of field 'DomainAxisLabel'.
     */
    public String getDomainAxisLabel(
    ) {
        return _domainAxisLabel;
    }

    /**
     * Returns the value of field 'drawBarOutline'.
     * 
     * @return the value of field 'DrawBarOutline'.
     */
    public boolean getDrawBarOutline(
    ) {
        return _drawBarOutline;
    }

    /**
     * Returns the value of field 'gridLines'.
     * 
     * @return the value of field 'GridLines'.
     */
    public GridLines getGridLines(
    ) {
        return _gridLines;
    }

    /**
     * Returns the value of field 'imageSize'.
     * 
     * @return the value of field 'ImageSize'.
     */
    public ImageSize getImageSize(
    ) {
        return _imageSize;
    }

    /**
     * Returns the value of field 'name'.
     * 
     * @return the value of field 'Name'.
     */
    public String getName(
    ) {
        return _name;
    }

    /**
     * Returns the value of field 'plotBackgroundColor'.
     * 
     * @return the value of field 'PlotBackgroundColor'.
     */
    public PlotBackgroundColor getPlotBackgroundColor(
    ) {
        return _plotBackgroundColor;
    }

    /**
     * Returns the value of field 'plotOrientation'.
     * 
     * @return the value of field 'PlotOrientation'.
     */
    public String getPlotOrientation(
    ) {
        return _plotOrientation;
    }

    /**
     * Returns the value of field 'rangeAxisLabel'.
     * 
     * @return the value of field 'RangeAxisLabel'.
     */
    public String getRangeAxisLabel(
    ) {
        return _rangeAxisLabel;
    }

    /**
     * Returns the value of field 'seriesColorClass'.
     * 
     * @return the value of field 'SeriesColorClass'.
     */
    public String getSeriesColorClass(
    ) {
        return _seriesColorClass;
    }

    /**
     * Method getSeriesDef.
     * 
     * @param index
     * @throws IndexOutOfBoundsException if the index
     * given is outside the bounds of the collection
     * @return the value of the
     * SeriesDef at the given index
     */
    public SeriesDef getSeriesDef(
            final int index)
    throws IndexOutOfBoundsException {
        // check bounds for index
        if (index < 0 || index >= _seriesDefList.size()) {
            throw new IndexOutOfBoundsException("getSeriesDef: Index value '" + index + "' not in range [0.." + (_seriesDefList.size() - 1) + "]");
        }
        
        return (SeriesDef) _seriesDefList.get(index);
    }

    /**
     * Method getSeriesDef.Returns the contents of the collection
     * in an Array.  <p>Note:  Just in case the collection contents
     * are changing in another thread, we pass a 0-length Array of
     * the correct type into the API call.  This way we <i>know</i>
     * that the Array returned is of exactly the correct length.
     * 
     * @return this collection as an Array
     */
    public SeriesDef[] getSeriesDef(
    ) {
        SeriesDef[] array = new SeriesDef[0];
        return (SeriesDef[]) _seriesDefList.toArray(array);
    }

    /**
     * Method getSeriesDefCollection.Returns a reference to
     * '_seriesDefList'. No type checking is performed on any
     * modifications to the Vector.
     * 
     * @return a reference to the Vector backing this class
     */
    public List<SeriesDef> getSeriesDefCollection(
    ) {
        return _seriesDefList;
    }

    /**
     * Method getSeriesDefCount.
     * 
     * @return the size of this collection
     */
    public int getSeriesDefCount(
    ) {
        return _seriesDefList.size();
    }

    /**
     * Returns the value of field 'showLegend'.
     * 
     * @return the value of field 'ShowLegend'.
     */
    public boolean getShowLegend(
    ) {
        return _showLegend;
    }

    /**
     * Returns the value of field 'showToolTips'.
     * 
     * @return the value of field 'ShowToolTips'.
     */
    public boolean getShowToolTips(
    ) {
        return _showToolTips;
    }

    /**
     * Returns the value of field 'showUrls'.
     * 
     * @return the value of field 'ShowUrls'.
     */
    public boolean getShowUrls(
    ) {
        return _showUrls;
    }

    /**
     * Returns the value of field 'subLabelClass'.
     * 
     * @return the value of field 'SubLabelClass'.
     */
    public String getSubLabelClass(
    ) {
        return _subLabelClass;
    }

    /**
     * Method getSubTitle.
     * 
     * @param index
     * @throws IndexOutOfBoundsException if the index
     * given is outside the bounds of the collection
     * @return the value of the
     * SubTitle at the given index
     */
    public SubTitle getSubTitle(
            final int index)
    throws IndexOutOfBoundsException {
        // check bounds for index
        if (index < 0 || index >= _subTitleList.size()) {
            throw new IndexOutOfBoundsException("getSubTitle: Index value '" + index + "' not in range [0.." + (_subTitleList.size() - 1) + "]");
        }
        
        return (SubTitle) _subTitleList.get(index);
    }

    /**
     * Method getSubTitle.Returns the contents of the collection in
     * an Array.  <p>Note:  Just in case the collection contents
     * are changing in another thread, we pass a 0-length Array of
     * the correct type into the API call.  This way we <i>know</i>
     * that the Array returned is of exactly the correct length.
     * 
     * @return this collection as an Array
     */
    public SubTitle[] getSubTitle(
    ) {
        SubTitle[] array = new SubTitle[0];
        return (SubTitle[]) _subTitleList.toArray(array);
    }

    /**
     * Method getSubTitleCollection.Returns a reference to
     * '_subTitleList'. No type checking is performed on any
     * modifications to the Vector.
     * 
     * @return a reference to the Vector backing this class
     */
    public List<SubTitle> getSubTitleCollection(
    ) {
        return _subTitleList;
    }

    /**
     * Method getSubTitleCount.
     * 
     * @return the size of this collection
     */
    public int getSubTitleCount(
    ) {
        return _subTitleList.size();
    }

    /**
     * Returns the value of field 'title'.
     * 
     * @return the value of field 'Title'.
     */
    public Title getTitle(
    ) {
        return _title;
    }

    /**
     * Returns the value of field 'variation'.
     * 
     * @return the value of field 'Variation'.
     */
    public String getVariation(
    ) {
        return _variation;
    }

    /**
     * Method hasDrawBarOutline.
     * 
     * @return true if at least one DrawBarOutline has been added
     */
    public boolean hasDrawBarOutline(
    ) {
        return _drawBarOutline != null;
    }

    /**
     * Method hasShowLegend.
     * 
     * @return true if at least one ShowLegend has been added
     */
    public boolean hasShowLegend(
    ) {
        return _showLegend != null;
    }

    /**
     * Method hasShowToolTips.
     * 
     * @return true if at least one ShowToolTips has been added
     */
    public boolean hasShowToolTips(
    ) {
        return _showToolTips != null;
    }

    /**
     * Method hasShowUrls.
     * 
     * @return true if at least one ShowUrls has been added
     */
    public boolean hasShowUrls(
    ) {
        return _showUrls != null;
    }

    /**
     * Overrides the Object.hashCode method.
     * <p>
     * The following steps came from <b>Effective Java Programming
     * Language Guide</b> by Joshua Bloch, Chapter 3
     * 
     * @return a hash code value for the object.
     */
    public int hashCode(
    ) {
        int result = 17;

        if (_name != null) {
           result = 37 * result + _name.hashCode();
        }
        if (_domainAxisLabel != null) {
           result = 37 * result + _domainAxisLabel.hashCode();
        }
        if (_rangeAxisLabel != null) {
           result = 37 * result + _rangeAxisLabel.hashCode();
        }
        if (_subLabelClass != null) {
           result = 37 * result + _subLabelClass.hashCode();
        }
        if (_seriesColorClass != null) {
           result = 37 * result + _seriesColorClass.hashCode();
        }
        result = 37 * result + (_drawBarOutline?0:1);
        result = 37 * result + (_showLegend?0:1);
        result = 37 * result + (_showToolTips?0:1);
        result = 37 * result + (_showUrls?0:1);
        if (_variation != null) {
           result = 37 * result + _variation.hashCode();
        }
        if (_plotOrientation != null) {
           result = 37 * result + _plotOrientation.hashCode();
        }
        if (_title != null) {
           result = 37 * result + _title.hashCode();
        }
        if (_imageSize != null) {
           result = 37 * result + _imageSize.hashCode();
        }
        if (_subTitleList != null) {
           result = 37 * result + _subTitleList.hashCode();
        }
        if (_gridLines != null) {
           result = 37 * result + _gridLines.hashCode();
        }
        if (_seriesDefList != null) {
           result = 37 * result + _seriesDefList.hashCode();
        }
        if (_plotBackgroundColor != null) {
           result = 37 * result + _plotBackgroundColor.hashCode();
        }
        if (_chartBackgroundColor != null) {
           result = 37 * result + _chartBackgroundColor.hashCode();
        }
        
        return result;
    }

    /**
     * Returns the value of field 'drawBarOutline'.
     * 
     * @return the value of field 'DrawBarOutline'.
     */
    public boolean isDrawBarOutline(
    ) {
        return _drawBarOutline != null ? _drawBarOutline : DEFAULT_DRAW_BAR_OUTLINE;
    }

    /**
     * Returns the value of field 'showLegend'.
     * 
     * @return the value of field 'ShowLegend'.
     */
    public boolean isShowLegend(
    ) {
        return _showLegend != null ? _showLegend : DEFAULT_SHOW_LEGEND;
    }

    /**
     * Returns the value of field 'showToolTips'.
     * 
     * @return the value of field 'ShowToolTips'.
     */
    public boolean isShowToolTips(
    ) {
        return _showToolTips != null ? _showToolTips : DEFAULT_SHOW_TOOLTIPS;
    }

    /**
     * Returns the value of field 'showUrls'.
     * 
     * @return the value of field 'ShowUrls'.
     */
    public boolean isShowUrls(
    ) {
        return _showUrls != null ? _showUrls : DEFAULT_SHOW_URLS;
    }

    /**
     * Method iterateSeriesDef.
     * 
     * @return an Iterator over all possible elements in this
     * collection
     */
    public Iterator<SeriesDef> iterateSeriesDef(
    ) {
        return _seriesDefList.iterator();
    }

    /**
     * Method iterateSubTitle.
     * 
     * @return an Iterator over all possible elements in this
     * collection
     */
    public Iterator<SubTitle> iterateSubTitle(
    ) {
        return _subTitleList.iterator();
    }

    /**
     */
    public void removeAllSeriesDef(
    ) {
        _seriesDefList.clear();
    }

    /**
     */
    public void removeAllSubTitle(
    ) {
        _subTitleList.clear();
    }

    /**
     * Method removeSeriesDef.
     * 
     * @param vSeriesDef
     * @return true if the object was removed from the collection.
     */
    public boolean removeSeriesDef(
            final SeriesDef vSeriesDef) {
        boolean removed = _seriesDefList.remove(vSeriesDef);
        return removed;
    }

    /**
     * Method removeSeriesDefAt.
     * 
     * @param index
     * @return the element removed from the collection
     */
    public SeriesDef removeSeriesDefAt(
            final int index) {
        Object obj = _seriesDefList.remove(index);
        return (SeriesDef) obj;
    }

    /**
     * Method removeSubTitle.
     * 
     * @param vSubTitle
     * @return true if the object was removed from the collection.
     */
    public boolean removeSubTitle(
            final SubTitle vSubTitle) {
        boolean removed = _subTitleList.remove(vSubTitle);
        return removed;
    }

    /**
     * Method removeSubTitleAt.
     * 
     * @param index
     * @return the element removed from the collection
     */
    public SubTitle removeSubTitleAt(
            final int index) {
        Object obj = _subTitleList.remove(index);
        return (SubTitle) obj;
    }

    /**
     * Sets the value of field 'chartBackgroundColor'.
     * 
     * @param chartBackgroundColor the value of field
     * 'chartBackgroundColor'.
     */
    public void setChartBackgroundColor(
            final ChartBackgroundColor chartBackgroundColor) {
        _chartBackgroundColor = chartBackgroundColor;
    }

    /**
     * Sets the value of field 'domainAxisLabel'.
     * 
     * @param domainAxisLabel the value of field 'domainAxisLabel'.
     */
    public void setDomainAxisLabel(
            final String domainAxisLabel) {
        _domainAxisLabel = domainAxisLabel;
    }

    /**
     * Sets the value of field 'drawBarOutline'.
     * 
     * @param drawBarOutline the value of field 'drawBarOutline'.
     */
    public void setDrawBarOutline(
            final boolean drawBarOutline) {
        _drawBarOutline = drawBarOutline;
    }

    /**
     * Sets the value of field 'gridLines'.
     * 
     * @param gridLines the value of field 'gridLines'.
     */
    public void setGridLines(
            final GridLines gridLines) {
        _gridLines = gridLines;
    }

    /**
     * Sets the value of field 'imageSize'.
     * 
     * @param imageSize the value of field 'imageSize'.
     */
    public void setImageSize(
            final ImageSize imageSize) {
        _imageSize = imageSize;
    }

    /**
     * Sets the value of field 'name'.
     * 
     * @param name the value of field 'name'.
     */
    public void setName(
            final String name) {
        _name = name;
    }

    /**
     * Sets the value of field 'plotBackgroundColor'.
     * 
     * @param plotBackgroundColor the value of field
     * 'plotBackgroundColor'.
     */
    public void setPlotBackgroundColor(
            final PlotBackgroundColor plotBackgroundColor) {
        _plotBackgroundColor = plotBackgroundColor;
    }

    /**
     * Sets the value of field 'plotOrientation'.
     * 
     * @param plotOrientation the value of field 'plotOrientation'.
     */
    public void setPlotOrientation(
            final String plotOrientation) {
        _plotOrientation = plotOrientation;
    }

    /**
     * Sets the value of field 'rangeAxisLabel'.
     * 
     * @param rangeAxisLabel the value of field 'rangeAxisLabel'.
     */
    public void setRangeAxisLabel(
            final String rangeAxisLabel) {
        _rangeAxisLabel = rangeAxisLabel;
    }

    /**
     * Sets the value of field 'seriesColorClass'.
     * 
     * @param seriesColorClass the value of field 'seriesColorClass'
     */
    public void setSeriesColorClass(
            final String seriesColorClass) {
        _seriesColorClass = seriesColorClass;
    }

    /**
     * 
     * 
     * @param index
     * @param vSeriesDef
     * @throws IndexOutOfBoundsException if the index
     * given is outside the bounds of the collection
     */
    public void setSeriesDef(
            final int index,
            final SeriesDef vSeriesDef)
    throws IndexOutOfBoundsException {
        // check bounds for index
        if (index < 0 || index >= _seriesDefList.size()) {
            throw new IndexOutOfBoundsException("setSeriesDef: Index value '" + index + "' not in range [0.." + (_seriesDefList.size() - 1) + "]");
        }
        
        _seriesDefList.set(index, vSeriesDef);
    }

    /**
     * 
     * 
     * @param vSeriesDefArray
     */
    public void setSeriesDef(
            final SeriesDef[] vSeriesDefArray) {
        //-- copy array
        _seriesDefList.clear();
        
        for (int i = 0; i < vSeriesDefArray.length; i++) {
                _seriesDefList.add(vSeriesDefArray[i]);
        }
    }

    /**
     * Sets the value of '_seriesDefList' by copying the given
     * Vector. All elements will be checked for type safety.
     * 
     * @param vSeriesDefList the Vector to copy.
     */
    public void setSeriesDef(
            final List<SeriesDef> vSeriesDefList) {
        // copy vector
        _seriesDefList.clear();
        
        _seriesDefList.addAll(vSeriesDefList);
    }

    /**
     * Sets the value of field 'showLegend'.
     * 
     * @param showLegend the value of field 'showLegend'.
     */
    public void setShowLegend(
            final boolean showLegend) {
        _showLegend = showLegend;
    }

    /**
     * Sets the value of field 'showToolTips'.
     * 
     * @param showToolTips the value of field 'showToolTips'.
     */
    public void setShowToolTips(
            final boolean showToolTips) {
        _showToolTips = showToolTips;
    }

    /**
     * Sets the value of field 'showUrls'.
     * 
     * @param showUrls the value of field 'showUrls'.
     */
    public void setShowUrls(
            final boolean showUrls) {
        _showUrls = showUrls;
    }

    /**
     * Sets the value of field 'subLabelClass'.
     * 
     * @param subLabelClass the value of field 'subLabelClass'.
     */
    public void setSubLabelClass(
            final String subLabelClass) {
        _subLabelClass = subLabelClass;
    }

    /**
     * 
     * 
     * @param index
     * @param vSubTitle
     * @throws IndexOutOfBoundsException if the index
     * given is outside the bounds of the collection
     */
    public void setSubTitle(
            final int index,
            final SubTitle vSubTitle)
    throws IndexOutOfBoundsException {
        // check bounds for index
        if (index < 0 || index >= _subTitleList.size()) {
            throw new IndexOutOfBoundsException("setSubTitle: Index value '" + index + "' not in range [0.." + (_subTitleList.size() - 1) + "]");
        }
        
        _subTitleList.set(index, vSubTitle);
    }

    /**
     * 
     * 
     * @param vSubTitleArray
     */
    public void setSubTitle(
            final SubTitle[] vSubTitleArray) {
        //-- copy array
        _subTitleList.clear();
        
        for (int i = 0; i < vSubTitleArray.length; i++) {
                _subTitleList.add(vSubTitleArray[i]);
        }
    }

    /**
     * Sets the value of '_subTitleList' by copying the given
     * Vector. All elements will be checked for type safety.
     * 
     * @param vSubTitleList the Vector to copy.
     */
    public void setSubTitle(
            final List<SubTitle> vSubTitleList) {
        // copy vector
        _subTitleList.clear();
        
        _subTitleList.addAll(vSubTitleList);
    }

    /**
     * Sets the value of field 'title'.
     * 
     * @param title the value of field 'title'.
     */
    public void setTitle(
            final Title title) {
        _title = title;
    }

    /**
     * Sets the value of field 'variation'.
     * 
     * @param variation the value of field 'variation'.
     */
    public void setVariation(
            final String variation) {
        _variation = variation;
    }

}
