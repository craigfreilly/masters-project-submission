(* ::Package:: *)

(************************************************************************)
(* This file was generated automatically by the Mathematica front end.  *)
(* It contains Initialization cells from a Notebook file, which         *)
(* typically will have the same name as this file except ending in      *)
(* ".nb" instead of ".m".                                               *)
(*                                                                      *)
(* This file is intended to be loaded into the Mathematica kernel using *)
(* the package loading commands Get or Needs.  Doing so is equivalent   *)
(* to using the Evaluate Initialization Cells menu command in the front *)
(* end.                                                                 *)
(*                                                                      *)
(* DO NOT EDIT THIS FILE.  This entire file is regenerated              *)
(* automatically each time the parent Notebook file is saved in the     *)
(* Mathematica front end.  Any changes you make to this file will be    *)
(* overwritten.                                                         *)
(************************************************************************)



BeginPackage["QuantumGroups`WeylGroups`",{"QuantumGroups`","QuantumGroups`RootSystems`"}];


PositiveRoots::usage="";


WeylGroup::usage="WeylGroup[\[CapitalGamma]] returns a list of matrices, representing the Weyl group elements in the fundamental weight basis.";


LongestWordDecomposition::usage="LongestWordDecomposition[\[CapitalGamma]] returns the lexicographically smallest decomposition of the longest element of the Weyl group.";


LongestWord::usage="LongestWord[\[CapitalGamma]] returns the longest element of the Weyl group, in the fundamental weight basis.";


Begin["`Private`"];


PositiveRoots[\[CapitalGamma]_]:=PositiveRoots[\[CapitalGamma]]=With[{l=LongestWordDecomposition[\[CapitalGamma]],s=SimpleRoots[\[CapitalGamma]]},
Table[
Fold[SimpleReflection[\[CapitalGamma],#2][#1]&, s[[l[[i]]]],Reverse[Take[l,i-1]]],{i,1,Length[l]}]]


WeylReflectionMatrix[\[CapitalGamma]_,i_]:=WeylReflectionMatrix[\[CapitalGamma],i]=Transpose[SimpleReflection[\[CapitalGamma],i]/@IdentityMatrix[Rank[\[CapitalGamma]]]]


WeylGroup[\[CapitalGamma]_]:=WeylGroup[\[CapitalGamma]]=Module[{indexedSimpleReflections=Table[{{i},WeylReflectionMatrix[\[CapitalGamma],i]},{i,1,Rank[\[CapitalGamma]]}],newElements,allElements={}},
newElements={{{},IdentityMatrix[Rank[\[CapitalGamma]]]}};
While[newElements!={},
allElements=allElements~Join~newElements;
newElements=Flatten[Outer[{#1[[1]]~Join~#2[[1]],#1[[2]].#2[[2]]}&,indexedSimpleReflections,newElements,1],1];
newElements=Union[Complement[newElements,allElements,SameTest->(#1[[2]]==#2[[2]]&)],SameTest->(#1[[2]]==#2[[2]]&)];
];
LongestWord[\[CapitalGamma]]=Last[allElements][[2]];
LongestWordDecomposition[\[CapitalGamma]]=Last[allElements][[1]];
Transpose[allElements][[2]]
]


LongestWord[\[CapitalGamma]_]:=(Dot@@(WeylReflectionMatrix[\[CapitalGamma],#]&/@LongestWordDecomposition[\[CapitalGamma]]))


LongestWordDecomposition[\[CapitalGamma]_]:=(WeylGroup[\[CapitalGamma]];LongestWordDecomposition[\[CapitalGamma]])


LongestWordDecomposition[Subscript[A, n_]]:=LongestWordDecomposition[Subscript[A, n]]=Flatten[Table[Reverse[Range[j]],{j,1,n}]]


LongestWordDecomposition[\[CapitalGamma]:Subscript[(B|C), n_]]:=LongestWordDecomposition[\[CapitalGamma]]=Flatten[Table[Take[Reverse[Range[n]],{Max[1,k],Min[k+n-1,n]}],{k,n,-n+2,-1}]]


LongestWordDecomposition[Subscript[D, n_]]:=LongestWordDecomposition[Subscript[D, n]]=Flatten[Table[Reverse[Range[j]],{j,1,n-2}]~Join~{n-1}~Join~Reverse[Range[n-2]]~Join~{n}~Join~Reverse[Range[n-2]]~Join~Table[{n-Mod[j,2]}~Join~Range[n-2,j+1,-1],{j,1,n-2}]]


LongestWordDecomposition[Subscript[E, 6]]={1,2,3,1,4,2,3,1,4,3,5,4,2,3,1,4,3,5,4,2,6,5,4,2,3,1,4,3,5,4,2,6,5,4,3,1};


LongestWordDecomposition[Subscript[F, 4]]={1,2,1,3,2,1,3,2,3,4,3,2,1,3,2,3,4,3,2,1,3,2,3,4};


LongestWordDecomposition[Subscript[G, 2]]={1,2,1,2,1,2};


End[];


EndPackage[];
