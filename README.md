# Ski-Reference-Backend

This is the updated backend repository of [ski-reference.com](http://ski-reference.com)

The deprecated repository can be found [here](https://github.com/seanp2/ski-reference)



## GET /results/:raceId
### Request
```
curl http://api.ski-reference.com/results/87408
```
### Response
```
{
    "date": {
        "day": 17,
        "month": 3,
        "year": 2017
    },
    "results": [
        {
            "birthyear": "1995",
            "nation": "USA",
            "lastfirstName": "PLANT Riley",
            "competitorID": 164803,
            "result": {
                "rank": "1",
                "score": 22.57,
                "difference": 0.0,
                "bib": 11,
                "combined": "125.45",
                "firstRun": "61.86",
                "secondRun": "63.59"
            },
            "previousPoints": 22.66,
            "scored": true
        },
        ...
     ]
}
```

## GET /comparison/:fisIdsCSV
### Request
```curl http://api.ski-reference.com/comparison/6532409,6532450```

```
{
  "sharedResults": [
    [
            {
                "discipline": "Giant Slalom",
                "score": 109.66,
                "venue": "Stowe Mountain Resort / Spruce Peak",
                "rank": "56",
                "raceID": "87524",
                "date": {
                    "day": 3,
                    "month": 4,
                    "year": 2017
                },
                "description": "Stowe Mountain Resort / Spruce Peak [Giant Slalom] Rank: 56 FIS points: 109.66",
                "csvrankAndScore": "56,109.66,"
            },
            {
                "discipline": "Giant Slalom",
                "score": 990.0,
                "venue": "Stowe Mountain Resort / Spruce Peak",
                "rank": "DNF2",
                "raceID": "87524",
                "date": {
                    "day": 3,
                    "month": 4,
                    "year": 2017
                },
                "description": "Stowe Mountain Resort / Spruce Peak [Giant Slalom] Rank: DNF2 FIS points: 990.0",
                "csvrankAndScore": "DNF2,990.0,"
            }
        ],
     ...
     ]
  
  ],
  "athletes": [
      {
        "allResults":[...],
        "name": "POMERANTZ, Sean",
        "compID": 203574,
      },
      {
        "allResults":[...],
        "name": "McNamara, Liam",
        "compID": 204800,
      }    
  ]
}

```
