import React, { useState, useMemo } from 'react';
import {
  PublicOffice,
  ROMANIAN_PUBLIC_OFFICES,
  getOfficesForProcedure,
  OfficeType,
} from '../data/publicOffices.ts';
import { Procedure } from '../types.ts';
import { Language } from '../i18n/translations.ts';
import {
  MapPin,
  Building2,
  Clock,
  Phone,
  Navigation,
  ExternalLink,
  Search,
  Filter,
  CheckCircle2,
  Info,
  Compass,
  Bus,
} from 'lucide-react';

interface PublicOfficesMapProps {
  procedure: Procedure;
  lang: Language;
}

export const PublicOfficesMap: React.FC<PublicOfficesMapProps> = ({
  procedure,
  lang,
}) => {
  const isRo = lang === 'ro';
  const [selectedCity, setSelectedCity] = useState<string>('toate');
  const [selectedOfficeId, setSelectedOfficeId] = useState<string | null>(null);
  const [searchFilter, setSearchFilter] = useState<string>('');

  // Relevant offices for this procedure
  const relevantOffices = useMemo(() => {
    return getOfficesForProcedure(procedure.id, procedure.category, selectedCity);
  }, [procedure.id, procedure.category, selectedCity]);

  // Filtered list by text search
  const filteredOffices = useMemo(() => {
    if (!searchFilter.trim()) return relevantOffices;
    const q = searchFilter.toLowerCase();
    return relevantOffices.filter(
      (o) =>
        o.name.toLowerCase().includes(q) ||
        o.address.toLowerCase().includes(q) ||
        o.city.toLowerCase().includes(q) ||
        o.typeLabelRo.toLowerCase().includes(q)
    );
  }, [relevantOffices, searchFilter]);

  // Active office selected
  const activeOffice = useMemo(() => {
    if (selectedOfficeId) {
      const found = filteredOffices.find((o) => o.id === selectedOfficeId);
      if (found) return found;
    }
    return filteredOffices[0] || null;
  }, [filteredOffices, selectedOfficeId]);

  // Available cities in dataset for this procedure
  const availableCities = useMemo(() => {
    const allForProc = getOfficesForProcedure(procedure.id, procedure.category);
    const cities = Array.from(new Set(allForProc.map((o) => o.city)));
    return ['toate', ...cities];
  }, [procedure.id, procedure.category]);

  const getMarkerColor = (type: OfficeType): { bg: string; border: string; text: string; pinFill: string } => {
    switch (type) {
      case 'SPCLEP':
        return { bg: 'bg-blue-100', border: 'border-blue-500', text: 'text-blue-700', pinFill: '#2563EB' };
      case 'DGP_PASAPOARTE':
        return { bg: 'bg-indigo-100', border: 'border-indigo-500', text: 'text-indigo-700', pinFill: '#4F46E5' };
      case 'DGPCI_AUTO':
        return { bg: 'bg-amber-100', border: 'border-amber-500', text: 'text-amber-800', pinFill: '#D97706' };
      case 'POLITIE_CAZIER':
        return { bg: 'bg-rose-100', border: 'border-rose-500', text: 'text-rose-700', pinFill: '#E11D48' };
      case 'ONRC':
        return { bg: 'bg-teal-100', border: 'border-teal-500', text: 'text-teal-800', pinFill: '#0D9488' };
      case 'ANAF':
        return { bg: 'bg-emerald-100', border: 'border-emerald-500', text: 'text-emerald-800', pinFill: '#059669' };
      case 'IGI':
        return { bg: 'bg-purple-100', border: 'border-purple-500', text: 'text-purple-800', pinFill: '#9333EA' };
      case 'STARE_CIVILA_DAS':
        return { bg: 'bg-cyan-100', border: 'border-cyan-500', text: 'text-cyan-800', pinFill: '#0891B2' };
      default:
        return { bg: 'bg-slate-100', border: 'border-slate-500', text: 'text-slate-800', pinFill: '#475569' };
    }
  };

  return (
    <div className="bg-white rounded-3xl border border-slate-200 shadow-sm overflow-hidden p-5 sm:p-7 space-y-6">
      {/* Header section */}
      <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4 border-b border-slate-100 pb-5">
        <div>
          <div className="inline-flex items-center gap-1.5 px-3 py-1 rounded-full text-xs font-bold bg-blue-50 text-blue-700 border border-blue-200 mb-2">
            <MapPin className="w-3.5 h-3.5 text-blue-600" />
            <span>{isRo ? 'Ghișee & Birouri Oficiale' : 'Public Offices & Desks'}</span>
          </div>
          <h3 className="text-xl sm:text-2xl font-extrabold text-slate-900 tracking-tight">
            {isRo ? 'Unde depui dosarul? Hartă și adrese fizice' : 'Where to apply? Office map & physical addresses'}
          </h3>
          <p className="text-xs sm:text-sm text-slate-500 mt-1 max-w-2xl leading-relaxed">
            {isRo
              ? `Locațiile oficiale competente pentru procedura „${procedure.title}”, cu orar de lucru cu publicul, detalii de transport și ghidaj GPS.`
              : `Official public desks competent for this procedure, with public visiting hours, transit directions, and GPS navigation.`}
          </p>
        </div>

        {/* Total found badge */}
        <div className="shrink-0 flex items-center gap-2 bg-slate-50 border border-slate-200 px-3.5 py-2 rounded-2xl">
          <Building2 className="w-4 h-4 text-slate-600" />
          <div className="text-xs">
            <span className="font-extrabold text-slate-900">{filteredOffices.length}</span>{' '}
            <span className="text-slate-500">{isRo ? 'locații afișate' : 'locations shown'}</span>
          </div>
        </div>
      </div>

      {/* Filter and City Chips */}
      <div className="space-y-3">
        <div className="flex flex-col sm:flex-row gap-3 items-stretch sm:items-center justify-between">
          {/* City selector chips */}
          <div className="flex items-center gap-1.5 overflow-x-auto pb-1 sm:pb-0 scrollbar-none">
            <span className="text-xs font-semibold text-slate-500 shrink-0 mr-1 flex items-center gap-1">
              <Compass className="w-3.5 h-3.5 text-slate-400" />
              {isRo ? 'Oraș:' : 'City:'}
            </span>
            {availableCities.map((city) => (
              <button
                key={city}
                onClick={() => {
                  setSelectedCity(city);
                  setSelectedOfficeId(null);
                }}
                className={`px-3 py-1.5 rounded-xl text-xs font-bold transition whitespace-nowrap ${
                  selectedCity === city
                    ? 'bg-blue-600 text-white shadow-sm'
                    : 'bg-slate-100 text-slate-700 hover:bg-slate-200'
                }`}
              >
                {city === 'toate' ? (isRo ? 'Toate orașele' : 'All cities') : city}
              </button>
            ))}
          </div>

          {/* Quick search input */}
          <div className="relative min-w-[200px]">
            <Search className="w-3.5 h-3.5 text-slate-400 absolute left-3 top-1/2 -translate-y-1/2" />
            <input
              type="text"
              value={searchFilter}
              onChange={(e) => setSearchFilter(e.target.value)}
              placeholder={isRo ? 'Filtrează după adresă sau nume...' : 'Filter by street or name...'}
              className="w-full pl-8 pr-3 py-1.5 text-xs bg-slate-50 border border-slate-200 rounded-xl focus:outline-none focus:ring-2 focus:ring-blue-500/20 focus:border-blue-500 transition"
            />
          </div>
        </div>
      </div>

      {/* Main View: Interactive Vector Map + Offices List */}
      <div className="grid grid-cols-1 lg:grid-cols-12 gap-6 items-start">
        
        {/* Interactive Stylized Map View (7 cols) */}
        <div className="lg:col-span-7 bg-gradient-to-b from-slate-900 to-slate-950 rounded-2xl p-4 sm:p-5 text-white shadow-inner relative overflow-hidden flex flex-col justify-between min-h-[360px] sm:min-h-[420px]">
          {/* Top map controls & legend */}
          <div className="flex flex-wrap items-center justify-between gap-2 z-10">
            <div className="flex items-center gap-2">
              <span className="w-2.5 h-2.5 rounded-full bg-emerald-400 animate-pulse" />
              <span className="text-xs font-bold text-slate-200 uppercase tracking-wider">
                {isRo ? 'Harta Ghișeelor din România' : 'Romania Public Desks Map'}
              </span>
            </div>
            <div className="text-[11px] text-slate-400 bg-slate-800/80 px-2.5 py-1 rounded-lg border border-slate-700">
              {isRo ? 'Selectează un pin pentru detalii' : 'Click marker for details'}
            </div>
          </div>

          {/* Stylized Romania Map Canvas with Vector Markers */}
          <div className="relative w-full h-[260px] sm:h-[300px] my-4 select-none">
            {/* SVG Country Contour */}
            <svg
              viewBox="0 0 100 70"
              className="w-full h-full opacity-20 filter drop-shadow-[0_4px_10px_rgba(0,0,0,0.5)]"
              preserveAspectRatio="xMidYMid meet"
            >
              <defs>
                <linearGradient id="roGradient" x1="0%" y1="0%" x2="100%" y2="100%">
                  <stop offset="0%" stopColor="#3B82F6" />
                  <stop offset="50%" stopColor="#6366F1" />
                  <stop offset="100%" stopColor="#1E293B" />
                </linearGradient>
              </defs>
              {/* Romania stylized geographic polygon contour */}
              <polygon
                points="22,12 35,8 48,6 62,5 78,8 86,16 93,24 88,38 92,48 88,58 78,62 68,64 54,66 40,64 26,62 14,58 8,46 10,34 14,24 18,16"
                fill="url(#roGradient)"
                stroke="#60A5FA"
                strokeWidth="1.2"
                strokeDasharray="2,2"
              />
              {/* Carpathian mountain arc indicator */}
              <path
                d="M 38,20 Q 55,24 64,36 Q 66,48 52,50"
                fill="none"
                stroke="#94A3B8"
                strokeWidth="0.8"
                strokeDasharray="1,1.5"
                opacity="0.6"
              />
            </svg>

            {/* City Hub Labels */}
            <div className="absolute top-[82%] left-[62%] -translate-x-1/2 -translate-y-1/2 text-[10px] font-bold text-slate-400 pointer-events-none">
              București
            </div>
            <div className="absolute top-[41%] left-[39%] -translate-x-1/2 -translate-y-1/2 text-[10px] font-bold text-slate-400 pointer-events-none">
              Cluj-Napoca
            </div>
            <div className="absolute top-[58%] left-[14%] -translate-x-1/2 -translate-y-1/2 text-[10px] font-bold text-slate-400 pointer-events-none">
              Timișoara
            </div>
            <div className="absolute top-[26%] left-[79%] -translate-x-1/2 -translate-y-1/2 text-[10px] font-bold text-slate-400 pointer-events-none">
              Iași
            </div>
            <div className="absolute top-[56%] left-[54%] -translate-x-1/2 -translate-y-1/2 text-[10px] font-bold text-slate-400 pointer-events-none">
              Brașov
            </div>

            {/* Interactive Office Marker Pins */}
            {filteredOffices.map((office, idx) => {
              const isSelected = activeOffice?.id === office.id;
              const colorInfo = getMarkerColor(office.type);
              
              // Slight visual offset if multiple markers in same city
              const jitterX = ((idx % 3) - 1) * 2.2;
              const jitterY = (((idx * 2) % 3) - 1) * 2.0;
              const posX = Math.max(8, Math.min(92, office.mapX + jitterX));
              const posY = Math.max(12, Math.min(88, office.mapY + jitterY));

              return (
                <div
                  key={office.id}
                  style={{ left: `${posX}%`, top: `${posY}%` }}
                  onClick={() => setSelectedOfficeId(office.id)}
                  className={`absolute -translate-x-1/2 -translate-y-1/2 cursor-pointer transition-all duration-300 z-20 group`}
                >
                  {/* Pulse ring for selected office */}
                  {isSelected && (
                    <span className="absolute -inset-2 rounded-full bg-blue-400/40 animate-ping pointer-events-none" />
                  )}

                  {/* Marker Pin Badge */}
                  <div
                    className={`relative flex items-center justify-center rounded-full transition-transform shadow-lg ${
                      isSelected
                        ? 'w-8 h-8 scale-125 ring-3 ring-white bg-blue-600 text-white'
                        : 'w-6 h-6 hover:scale-115 bg-slate-800 border border-slate-600 text-slate-200'
                    }`}
                    style={{
                      backgroundColor: isSelected ? colorInfo.pinFill : undefined,
                    }}
                  >
                    <MapPin className={`${isSelected ? 'w-4 h-4' : 'w-3 h-3'}`} />
                  </div>

                  {/* Mini Hover Callout */}
                  <div className="absolute bottom-full left-1/2 -translate-x-1/2 mb-2 hidden group-hover:block z-30 pointer-events-none">
                    <div className="bg-slate-900 text-white text-[11px] font-bold px-2.5 py-1 rounded-md shadow-xl border border-slate-700 whitespace-nowrap">
                      {office.name.split('-')[0].trim()}
                    </div>
                  </div>
                </div>
              );
            })}
          </div>

          {/* Active Marker Preview Card on the Map */}
          {activeOffice && (
            <div className="bg-slate-800/95 backdrop-blur-md border border-slate-700/80 rounded-xl p-3 z-10 flex items-center justify-between gap-3 animate-in fade-in slide-in-from-bottom-2 duration-200">
              <div className="flex items-center gap-2.5 min-w-0">
                <div
                  className="w-8 h-8 rounded-lg flex items-center justify-center shrink-0"
                  style={{ backgroundColor: getMarkerColor(activeOffice.type).pinFill }}
                >
                  <Building2 className="w-4 h-4 text-white" />
                </div>
                <div className="min-w-0">
                  <div className="text-xs font-bold text-white truncate">{activeOffice.name}</div>
                  <div className="text-[11px] text-slate-300 truncate">{activeOffice.address}</div>
                </div>
              </div>

              <a
                href={`https://www.google.com/maps/search/?api=1&query=${encodeURIComponent(activeOffice.googleMapsQuery)}`}
                target="_blank"
                rel="noopener noreferrer"
                className="shrink-0 inline-flex items-center gap-1.5 px-3 py-1.5 rounded-lg bg-blue-600 hover:bg-blue-700 text-white text-xs font-bold transition"
              >
                <Navigation className="w-3.5 h-3.5" />
                <span>{isRo ? 'Navighează' : 'Navigate'}</span>
              </a>
            </div>
          )}
        </div>

        {/* Relevant Offices Detail List (5 cols) */}
        <div className="lg:col-span-5 space-y-3">
          <div className="flex items-center justify-between text-xs font-bold text-slate-700 px-1">
            <span>{isRo ? 'Locații disponibile pentru această procedură:' : 'Available desks for this procedure:'}</span>
            <span className="text-slate-400 font-normal">
              {filteredOffices.length} {isRo ? 'rezultate' : 'results'}
            </span>
          </div>

          {filteredOffices.length === 0 ? (
            <div className="text-center py-10 bg-slate-50 rounded-2xl border border-dashed border-slate-200 p-6">
              <Info className="w-8 h-8 text-slate-400 mx-auto mb-2" />
              <p className="text-sm font-bold text-slate-700">
                {isRo ? 'Nu au fost găsite ghișee în acest oraș' : 'No desks found for this city'}
              </p>
              <p className="text-xs text-slate-500 mt-1">
                {isRo
                  ? 'Alege opțiunea „Toate orașele” sau caută altă localitate.'
                  : 'Select "All cities" to view country-wide locations.'}
              </p>
            </div>
          ) : (
            <div className="space-y-3 max-h-[440px] overflow-y-auto pr-1">
              {filteredOffices.map((office) => {
                const isSelected = activeOffice?.id === office.id;
                const colors = getMarkerColor(office.type);

                return (
                  <div
                    key={office.id}
                    onClick={() => setSelectedOfficeId(office.id)}
                    className={`p-4 rounded-2xl border transition-all cursor-pointer ${
                      isSelected
                        ? 'border-blue-600 bg-blue-50/50 shadow-md ring-1 ring-blue-500'
                        : 'border-slate-200 bg-white hover:border-slate-300 hover:shadow-xs'
                    }`}
                  >
                    {/* Badge and City */}
                    <div className="flex items-center justify-between gap-2 mb-2">
                      <span className={`text-[10px] font-extrabold uppercase px-2 py-0.5 rounded-md ${colors.bg} ${colors.text}`}>
                        {isRo ? office.typeLabelRo : office.typeLabelEn}
                      </span>
                      <span className="text-xs font-bold text-slate-600 flex items-center gap-1">
                        <MapPin className="w-3 h-3 text-slate-400" />
                        {office.city}
                      </span>
                    </div>

                    {/* Name */}
                    <h4 className="text-sm font-bold text-slate-900 leading-snug">
                      {isRo ? office.name : (office.nameEn || office.name)}
                    </h4>

                    {/* Address */}
                    <p className="text-xs text-slate-600 mt-1.5 flex items-start gap-1.5">
                      <span className="font-semibold text-slate-400 shrink-0">📍</span>
                      <span>{office.address}</span>
                    </p>

                    {/* Public transit info */}
                    {(office.publicTransitRo || office.publicTransitEn) && (
                      <p className="text-[11px] text-blue-900 bg-blue-50/70 rounded-lg p-2 mt-2 flex items-start gap-1.5 border border-blue-100">
                        <Bus className="w-3.5 h-3.5 text-blue-600 shrink-0 mt-0.5" />
                        <span>{isRo ? office.publicTransitRo : (office.publicTransitEn || office.publicTransitRo)}</span>
                      </p>
                    )}

                    {/* Schedule */}
                    <div className="text-[11px] text-slate-500 mt-2 flex items-start gap-1.5">
                      <Clock className="w-3.5 h-3.5 text-slate-400 shrink-0 mt-0.5" />
                      <span>{isRo ? office.scheduleRo : office.scheduleEn}</span>
                    </div>

                    {/* Action buttons */}
                    <div className="mt-3 pt-3 border-t border-slate-100 flex flex-wrap items-center gap-2">
                      <a
                        href={`https://www.google.com/maps/search/?api=1&query=${encodeURIComponent(office.googleMapsQuery)}`}
                        target="_blank"
                        rel="noopener noreferrer"
                        className="inline-flex items-center gap-1 text-xs font-bold text-blue-700 hover:text-blue-900 bg-white border border-blue-200 px-2.5 py-1 rounded-lg transition"
                      >
                        <Navigation className="w-3 h-3" />
                        <span>{isRo ? 'Google Maps' : 'Google Maps'}</span>
                      </a>

                      {office.appointmentUrl && (
                        <a
                          href={office.appointmentUrl}
                          target="_blank"
                          rel="noopener noreferrer"
                          className="inline-flex items-center gap-1 text-xs font-bold text-emerald-700 hover:text-emerald-900 bg-emerald-50 border border-emerald-200 px-2.5 py-1 rounded-lg transition"
                        >
                          <ExternalLink className="w-3 h-3" />
                          <span>{isRo ? 'Programare online' : 'Book slot'}</span>
                        </a>
                      )}

                      {office.phone && (
                        <a
                          href={`tel:${office.phone.replace(/\s+/g, '')}`}
                          className="inline-flex items-center gap-1 text-xs font-medium text-slate-600 hover:text-slate-900 px-2 py-1"
                        >
                          <Phone className="w-3 h-3" />
                          <span>{office.phone}</span>
                        </a>
                      )}
                    </div>
                  </div>
                );
              })}
            </div>
          )}
        </div>
      </div>
    </div>
  );
};
