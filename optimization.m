(* Define Parameters *)
s = 0.8;
n = 0.6;
pbiased = 0.75;
rcognitive = 0.3;
refficiency = 0.2;

(* Define Function *)
psi[alpha_, lambda1_, lambda2_] := 
  (alpha*s + (1 - alpha)*n) * 
  Exp[-(lambda1*rcognitive + lambda2*refficiency)] * pbiased;

(* Run Optimization *)
NMaximize[
  {
    psi[alpha, lambda1, lambda2],
    0 <= alpha <= 1 && 
    lambda1 >= 0 && lambda2 >= 0 && 
    lambda1 + lambda2 <= 2
  },
  {alpha, lambda1, lambda2}
]
