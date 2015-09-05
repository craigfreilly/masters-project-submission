#!/usr/local/bin/WolframScript -script

<< KnotTheory`
x = $ScriptCommandLine[[2]]
ap = DrawPD[GaussCode[ReadList[x, Number]], {Gap -> 0.03}]
draw = ap;
Export["../resources/temp/planarPic.jpg", draw]