using System;
using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;

namespace CityCheckAPI.Controllers.Model
{
    public class Team
    {
        public int TeamId { get; set; }
        public int Punten { get; set; }
        public string Kleur { get; set; }
        public string TeamNaam { get; set; }
        public Locatie HuidigeLocatie { get; set;}
        public TeamTrace TeamTrace { get; set; }
    }
}
