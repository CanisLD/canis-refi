# canis-refi
loan amortization calculation for refinancing


for aws container deployment, run commands
Notes: https://gallery.ecr.aws/lambda/java

`mvn compile dependency:copy-dependencies -DincludeScope=runtime`
`docker build -f <dockerfile> -t <image name> .`
`docker run -p 9000:8080 <image name>`

test with curl.

// the url is not be correct here. look up what gets built.

`curl -XPOST "http://localhost:9000/2015-03-31/functions/function/invocations" -d '{"payload":"hello world!"}'`

// test payload for canis-refi
```
curl -X POST "http://localhost:9000/2015-03-31/functions/function/invocations" \
-H 'Content-Type: application/json; charset=utf-8' -d @- <<BODY
{ 
  "takeNth": 12,
  "currentLoan":
  {
    "label": "Original Loan A",
    "start": 0,
    "end": 70,
    "cost": 0.00,
    "loanDetails":
    {
      "amount": 500000,
      "interestRate": 0.035,
      "paymentFrequency": 12,
      "numberOfTerms": 30
    }
  }, 
  "refinanceLoan": 
  {
    "label":"Refinance Loan B",
    "start": 70,
    "end": 430,
    "cost": 0.00,
    "loanDetails":
    {
      "amount": 440000,
      "interestRate": 0.03, 
      "paymentFrequency": 12,
      "numberOfTerms": 30
    }
  } 
}
BODY
```

test serverless API GateWay Set up.

`https://zchwjei8rc.execute-api.us-west-1.amazonaws.com/beta/loans`

```
curl -X POST "https://zchwjei8rc.execute-api.us-west-1.amazonaws.com/beta/loans" \
-H 'Content-Type: application/json; charset=utf-8' -d @- <<BODY
{ 
  "takeNth": 12,
  "currentLoan":
  {
    "label": "Original Loan A",
    "start": 0,
    "end": 70,
    "cost": 0.00,
    "loanDetails":
    {
      "amount": 500000,
      "interestRate": 0.035,
      "paymentFrequency": 12,
      "numberOfTerms": 30
    }
  }, 
  "refinanceLoan": 
  {
    "label":"Refinance Loan B",
    "start": 70,
    "end": 430,
    "cost": 0.00,
    "loanDetails":
    {
      "amount": 440000,
      "interestRate": 0.03, 
      "paymentFrequency": 12,
      "numberOfTerms": 30
    }
  } 
}
BODY
```