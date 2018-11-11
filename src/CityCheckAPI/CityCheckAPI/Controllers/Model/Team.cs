using System;
using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;

namespace CityCheckAPI.Controllers.Model
{
    public class Team
    {
        public int Id { get; set; }
        public int Punten { get; set; }
        public int Kleur { get; set; }
        public string TeamNaam { get; set; }
        public long HuidigeLong { get; set; }
        public long HuidigeLat { get; set; }
        public List<TeamTrace> TeamTraces { get; set; }
    }
}
