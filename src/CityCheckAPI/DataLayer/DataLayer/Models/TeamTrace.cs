﻿using System.Collections.Generic;
using System.ComponentModel.DataAnnotations.Schema;
using System.Linq;
using System.Threading.Tasks;

namespace DataLayer
{
    public class TeamTrace
    {
        public int Id { get; set; }
        /* [ForeignKey("TeamId")]
         public virtual Team Team { get; set; }*/
        public Locatie trace { get; set; }

        public TeamTrace(Locatie locatie)
        {
            trace = locatie;
        }
    }
}
