#!/usr/local/bin/WolframScript -script

<< KnotTheory`
x = $ScriptCommandLine[[2]]
ap = ArcPresentation[GaussCode[ReadList[x, Number]]]
draw = Draw[ap];
Export["../resources/temp/arcPic.jpg", draw]