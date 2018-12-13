using System;
using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;

namespace DataLayer
{
    public class Team
    {
        public int Id { get; set; }
        public int Punten { get; set; }
        public int Kleur { get; set; }
        public string TeamNaam { get; set; }
        public Locatie huidigeLocatie { get; set; }
        public List<TeamTrace> TeamTraces { get; set; }
    }
}
